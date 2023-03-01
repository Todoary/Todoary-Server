#!/usr/bin/env bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh
source ${ABSDIR}/switch.sh
source ${ABSDIR}/stop.sh

IDLE_PORT=$(find_idle_port)

echo "> Health Check Start!"
echo "> IDLE_PORT: $IDLE_PORT"
echo "> curl -s http://localhost:$IDLE_PORT/profile "
sleep 10

for RETRY_COUNT in {1..10}
do
  RESPONSE=$(curl -s http://localhost:${IDLE_PORT}/profile)
  UP_COUNT=$(echo ${RESPONSE} | grep 'release' | wc -l)

  if [ ${UP_COUNT} -ge 1 ]
  then # $up_count >= 1 ("release" 문자열이 있다)
    echo "> Health check 성공"
    switch_proxy # switch.sh의 함수. nginx의 service_url 변경 후 reload
    # 이제 IDLE_PORT로 새 부트가 떴으므로 다른 포트는 종료시킴
    if [ "$IDLE_PORT" == "9001" ]; then
      stop_port "9002"
    else
      stop_port "9001"
    fi
    break
  else
    echo "> Health check의 응답을 알 수 없거나 혹은 실행 상태가 아닙니다."
    echo "> Health check: ${RESPONSE}"
  fi

  if [ ${RETRY_COUNT} -eq 10 ]
    then
      echo "> Health check 실패. "
      echo "> 엔진엑스에 연결하지 않고 배포를 종료합니다."
      exit 1
  fi

  echo "> Health check 연결 실패. 재시도..."
  sleep 10
done
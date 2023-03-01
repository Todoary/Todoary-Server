#!/usr/bin/env bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh

stop_port()
{
  if [ -z "$1" ]; then
    echo "> idle port 찾기"
    IDLE_PORT=$(find_idle_port)
  else
    IDLE_PORT=$1
  fi

  echo "> $IDLE_PORT 에서 구동 중인 애플리케이션 pid 확인"
  echo "lsof -ti tcp:$IDLE_PORT"
  IDLE_PID=$(lsof -ti tcp:"$IDLE_PORT")

  if [ -z ${IDLE_PID} ]; then
    echo "> 현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
  else
    echo "> kill -15 $IDLE_PID"
    kill -15 ${IDLE_PID}
    sleep 5
  fi
}

stop_port "$1"
#!/usr/bin/env bash

# bash는 return value가 안되니 *제일 마지막줄에 echo로 결과 출력*후, 클라이언트에서 값 사용

# 쉬고 있는 profile 찾기: release1이 사용중이면 release2가 쉬고 있고, 반대면 release1이 쉬고 있음

# function 함수명() 으로 해도 되지만 생략 가능하다.
find_idle_profile() {
  RESPONSE_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost/profile)

  if [ "${RESPONSE_CODE}" -ge 400 ] # 400보다 크면, 즉 40X 50X 에러 모두 포함
  then
    CURRENT_PROFILE=release2
  else
    CURRENT_PROFILE=$(curl -s http://localhost/profile)
  fi

  if [ "${CURRENT_PROFILE}" == release1 ]
  then
    IDLE_PROFILE=release2
  else
    IDLE_PROFILE=release1
  fi

  echo "${IDLE_PROFILE}"
}

find_idle_port()
{
  IDLE_PROFILE=$(find_idle_profile)

  if [ "${IDLE_PROFILE}" == release1 ]
  then
    echo "9001"
  else
    echo "9002"
  fi
}

find_not_idle_port()
{
    IDLE_PROFILE=$(find_idle_profile)

    if [ "${IDLE_PROFILE}" == release1 ]
    then
      echo "9002"
    else
      echo "9001"
    fi
}
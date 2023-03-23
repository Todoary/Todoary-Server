# Todoary-Server

### Todoary GitHub flow

![GitHub flow](https://user-images.githubusercontent.com/100466546/175521203-d908528d-8539-425f-adf2-ca11cea2a614.png)

- Develop : 최종 런칭 버전을 개발하는 **메인 브렌치**

      → Feature브랜치에서 개발한 내용을 머지합니다.

      → 머지를 하기 위해서는 팀원 1명의 승인이 필요합니다.

      → PR 코드 리뷰 필수 입니다. 

- Feature : 기능을 개발하는 브랜치 
- Bugfix : 최종 런칭 버전개발 과정에서 발생한 버그를 수정하는 브렌치
- Release : 제품으로 출시될 수 있는 브랜치
- Hotfix : 출시 버전에서 발생한 버그를 수정 하는 브랜치

---------------------------

### Todoary commit 컨벤션

- feat -  새 기능 구현
- fix - 에러 수정
- style - 코드 변경이 없는 경우, 코드 포멧팅
- refactor - 코드 리펙토링
- rename - 파일, 폴더명만 수정하거나 이동하는 작업
- test - 테스트 관련 작업

**Subject**

- 과거시제를 사용하지 않고 명령어로 작성 (ex : Fixed → Fix,  Added → Add)

**commit message**

- 커밋에서 작업한 내용을 간략하게 설명합니다.
- 선택사항이기 때문에 모든 커밋에 작성할 필요는 없습니다.
- 제목과 구분되도록 한칸 띄워 작성합니다.

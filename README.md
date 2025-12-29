# 12th-Naengjango-BE

## 브랜치 전략

- `main`  
  - 배포 브랜치
  - 직접 push 금지
  - Pull Request로만 병합

- `develop`  
  - 개발 통합 브랜치
  - 모든 feature 브랜치는 develop에서 분기

- `feature/*`  
  - 기능 단위 브랜치
  - 작업 완료 후 develop으로 PR

## 커밋 컨벤션
| type | 의미 | 예시 |
| --- | --- | --- |
| **feat** | 새로운 기능 | 로그인 API 구현 |
| **fix** | 버그 수정 | NPE 해결 |
| **docs** | 문서 수정 | README 업데이트 |
| **chore** | 빌드·설정 변경 | Gradle 설정 변경 |
| **refactor** | 기능 변화 없는 코드 리팩터링 | Service 분리 |
| **style** | 포맷/세미콜론/네이밍 등 | 포맷팅 |
| **test** | 테스트 코드 | Controller 단위 테스트 |
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
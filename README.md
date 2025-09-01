
# ds-java-lib (Groovy DSL)

Java Data Structures as a reusable library (Gradle Groovy DSL).

## Modules
- `ds-core`: public API and implementations
- `ds-examples`: tiny console app that consumes the library

## Build
```bash
# run tests
./gradlew :ds-core:test

# run example app → prints: 1, 99, 2
./gradlew :ds-examples:run
```

## Publish to local Maven
```bash
./gradlew :ds-core:publishToMavenLocal
```
Then depend on `io.github.jho951:ds-core:0.1.0` from other projects.


# 사용 법
## 1) 클린 빌드 & 테스트
./gradlew clean
./gradlew :ds-core:test

## 2) 예제 앱 실행 (출력으로 동작 확인)
./gradlew :ds-examples:run

## 3) 로컬 Maven에 퍼블리시 → 다른 프로젝트에서 의존성으로 사용
./gradlew :ds-core:publishToMavenLocal

# (macOS) gradlew 실행 권한 없으면 한 번만
chmod +x gradlew

# 1) 라이브러리 테스트(기능 검증)
./gradlew :ds-core:test

# 2) 예제 앱 실행(눈으로 확인)
./gradlew :ds-examples:run

# 3) 라이브러리를 로컬 저장소에 배포 → 다른 프로젝트에서 의존성으로 사용
./gradlew :ds-core:publishToMavenLocal

## list , queue, stack, tree 

# hanghaePlus_2024
10 weeks of Hanghae Plus backend corse

### 📌1주차 : TDD 개발
#### [과제] `point` 패키지의 TODO 와 테스트코드를 작성해주세요.

**요구 사항**

- PATCH  `/point/{id}/charge` : 포인트를 충전한다.
- PATCH `/point/{id}/use` : 포인트를 사용한다.
- GET `/point/{id}` : 포인트를 조회한다.
- GET `/point/{id}/histories` : 포인트 내역을 조회한다.
- 잔고가 부족할 경우, 포인트 사용은 실패하여야 합니다.
- 동시에 여러 건의 포인트 충전, 이용 요청이 들어올 경우 순차적으로 처리되어야 합니다. (동시성)

<br />

### 📌 2주차 : Clean Achitechure 개발
#### Description
특강 신청 서비스를 구현해 봅니다.<br />
항해 플러스 토요일 특강을 신청할 수 있는 서비스를 개발합니다.<br />
특강 신청 및 신청자 목록 관리를 RDBMS를 이용해 관리할 방법을 고민합니다.<br />
#### Requirements
아래 2가지 API 를 구현합니다.<br />
특강 신청 API<br />
특강 신청 여부 조회 API<br />
각 기능 및 제약 사항에 대해 단위 테스트를 반드시 하나 이상 작성하도록 합니다.<br />
다수의 인스턴스로 어플리케이션이 동작하더라도 기능에 문제가 없도록 작성하도록 합니다.<br />
동시성 이슈를 고려하여 구현합니다.<br />
#### API Specs
1️⃣ (핵심) 특강 신청 API POST /lectures/apply<br />
<br />
특정 userId 로 선착순으로 제공되는 특강을 신청하는 API 를 작성합니다.<br />
동일한 신청자는 한 번의 수강 신청만 성공할 수 있습니다.<br />
각 강의는 선착순 30명만 신청 가능합니다.<br />
이미 신청자가 30명이 초과되면 이후 신청자는 요청을 실패합니다.<br />
어떤 유저가 특강을 신청했는지 히스토리를 저장해야한다.<br />
<br />
2️⃣ (기본) 특강 목록 API GET /lectures<br />
<br />
단 한번의 특강을 위한 것이 아닌 날짜별로 특강이 존재할 수 있는 범용적인 서비스로 변화시켜 봅니다.<br />
이를 수용하기 위해, 특강 엔티티의 경우 기본 과제 SPEC 을 만족하는 설계에서 변경되어야 할 수 있습니다.<br />
수강신청 API 요청 및 응답 또한 이를 잘 수용할 수 있는 구조로 변경되어야 할 것입니다.<br />
특강의 정원은 30명으로 고정이며, 사용자는 각 특강에 신청하기전 목록을 조회해볼 수 있어야 합니다.<br />
추가로 정원이 특강마다 다르다면 어떻게 처리할것인가..? 고민해 보셔라~<br />
<br />
3️⃣ (기본) 특강 신청 완료 여부 조회 API GET /lectures/application/{userId}<br />
<br />
특정 userId 로 특강 신청 완료 여부를 조회하는 API 를 작성합니다.<br />
특강 신청에 성공한 사용자는 성공했음을, 특강 등록자 명단에 없는 사용자는 실패했음을 반환합니다. (true, false)<br />
<br />
💡 KEY POINT<br />
정확하게 30명의 사용자에게만 특강을 제공할 방법을 고민해 봅니다.<br />
같은 사용자에게 여러 번의 특강 슬롯이 제공되지 않도록 제한할 방법을 고민해 봅니다.<br />

<br />

### 📌 3~5주차 : 콘서트 예약 서비스 개발
#### Description
`콘서트 예약 서비스`를 구현해 봅니다.<br />
대기열 시스템을 구축하고, 예약 서비스는 작업가능한 유저만 수행할 수 있도록 해야합니다.<br />
사용자는 좌석예약 시에 미리 충전한 잔액을 이용합니다.<br />
좌석 예약 요청시에, 결제가 이루어지지 않더라도 일정 시간동안 다른 유저가 해당 좌석에 접근할 수 없도록 합니다.<br />
#### Requirements
아래 5가지 API 를 구현합니다.<br />
- 유저 토큰 발급 API
- 예약 가능 날짜 / 좌석 API
- 좌석 예약 요청 API
- 잔액 충전 / 조회 API
- 결제 API
각 기능 및 제약사항에 대해 단위 테스트를 반드시 하나 이상 작성하도록 합니다.<br />
다수의 인스턴스로 어플리케이션이 동작하더라도 기능에 문제가 없도록 작성하도록 합니다.<br />
동시성 이슈를 고려하여 구현합니다.<br />
대기열 개념을 고려해 구현합니다.<br />
#### API Specs
1️⃣ `주요` 유저 대기열 토큰 기능<br />
<br />
서비스를 이용할 토큰을 발급받는 API를 작성합니다.<br />
토큰은 유저의 UUID 와 해당 유저의 대기열을 관리할 수 있는 정보 ( 대기 순서 or 잔여 시간 등 ) 를 포함합니다.<br />
이후 모든 API 는 위 토큰을 이용해 대기열 검증을 통과해야 이용 가능합니다.<br />
> 기본적으로 폴링으로 본인의 대기열을 확인한다고 가정하며, 다른 방안 또한 고려해보고 구현해 볼 수 있습니다.<br />
<br />
2️⃣ `기본` 예약 가능 날짜 / 좌석 API<br />
예약가능한 날짜와 해당 날짜의 좌석을 조회하는 API 를 각각 작성합니다.<br />
예약 가능한 날짜 목록을 조회할 수 있습니다.<br />
날짜 정보를 입력받아 예약가능한 좌석정보를 조회할 수 있습니다.<br />
> 좌석 정보는 1 ~ 50 까지의 좌석번호로 관리됩니다.<br />
<br />
3️⃣ `주요` 좌석 예약 요청 API<br />
<br />
날짜와 좌석 정보를 입력받아 좌석을 예약 처리하는 API 를 작성합니다.<br />
좌석 예약과 동시에 해당 좌석은 그 유저에게 약 5분간 임시 배정됩니다. ( 시간은 정책에 따라 자율적으로 정의합니다. )<br />
만약 배정 시간 내에 결제가 완료되지 않는다면 좌석에 대한 임시 배정은 해제되어야 하며 다른 사용자는 예약할 수 없어야 한다.<br />
<br />
4️⃣ `기본` 잔액 충전 / 조회 API<br />
<br />
결제에 사용될 금액을 API 를 통해 충전하는 API 를 작성합니다.<br />
사용자 식별자 및 충전할 금액을 받아 잔액을 충전합니다.<br />
사용자 식별자를 통해 해당 사용자의 잔액을 조회합니다.<br />
<br />
5️⃣ `주요` 결제 API<br />
<br />
결제 처리하고 결제 내역을 생성하는 API 를 작성합니다.<br />
결제가 완료되면 해당 좌석의 소유권을 유저에게 배정하고 대기열 토큰을 만료시킵니다.<br />
<br />
💡 KEY POINT<br />
유저간 대기열을 요청 순서대로 정확하게 제공할 방법을 고민해 봅니다.<br />
동시에 여러 사용자가 예약 요청을 했을 때, 좌석이 중복으로 배정 가능하지 않도록 합니다.<br />

<br />

### 📌 6주차 : 동시성 문제와 극복
#### [과제] 
- 나의 시나리오에서 발생할 수 있는 동시성 이슈에 대해 파악하고 가능한 동시성 제어 방식들을 도입해보고 각각의 장단점을 파악한 내용을 정리 제출
  - 구현의 복잡도, 성능, 효율성 등
  - README 작성 혹은 외부 링크, 프로젝트 내의 다른 문서에 작성하였다면 README에 링크 게재
- **DB Lock 을 활용한 동시성 제어 방식** 에서 해당 비즈니스 로직에서 적합하다고 판단하여 차용한 동시성 제어 방식을 구현하여 비즈니스 로직에 적용하고, 통합테스트 등으로 이를 검증하는 코드 작성 및 제출

<br />

### 📌 7주차: 적은 부하로 트래픽 처리하기
#### [과제] 
- 조회가 오래 걸리는 쿼리에 대한 캐싱, 혹은 Redis 를 이용한 로직 이관을 통해 성능 개선할 수 있는 로직을 분석하고 이를 합리적인 이유와 함께 정리한 문서 제출
- 대기열 구현에 대한 설계를 진행하고, 설계한 내용과 부합하도록 적절하게 동작하는 대기열을 구현하여 제출   
  - Redis, Queue, MQ 등 DB가 아닌 다른 수단을 활용해 대기열 개선 설계 및 구현

<br />

### 📌 8주차: 부하를 적절하게 축소하기
#### [과제] 
- 나의 시나리오에서 수행하는 쿼리들을 수집해보고, 필요하다고 판단되는 인덱스를 추가하고 쿼리의 성능개선 정도를 작성하여 제출
  - 자주 조회하는 쿼리, 복잡한 쿼리 파악
  - Index 추가 전후 Explain, 실행시간 등 비교
- 내가 개발한 기능의 트랜잭션 범위에 대해 이해하고, 서비스의 규모가 확장되어 MSA 형태로 서비스를 분리한다면 어떤 서비스로 분리 확장될지 설계하고, 그 분리에 따른 트랜잭션 처리의 한계와 해결방안에 대한 서비스 설계문서 작성
  - 실시간 주문, 좌석예약 정보를 데이터 플랫폼에 전달하는 ( 외부 API 호출, 메세지 발행 등 ) 요구사항 등을 기존 로직에 추가해 보고 기존 로직에 영향 없이 부가 기능을 제공

<br />

### 📌 9주차: 책임 분리를 통한 애플리케이션 설계
#### [과제] 
- docker 를 이용해 kafka 를 설치 및 실행하고 애플리케이션과 연결
  - 각 프레임워크 (nest.js, spring) 에 적합하게 카프카 consumer, producer 를 연동 및 테스트
- 기존에 애플리케이션 이벤트를 카프카 메세지 발행으로 변경
  - 카프카의 발행이 실패하는 것을 방지하기 위해 Transactional Outbox Pattern를 적용
  - 카프카의 발행이 실패한 케이스에 대한 재처리를 구현 ( Scheduler or BatchProcess )

<br />

### 📌 10주차: 장애 대응
#### [과제]
- 부하 테스트 대상 선정 및 목적, 시나리오 등의 계획을 세우고 이를 문서로 작성
  - 적합한 테스트 스크립트를 작성하고 수행
  > `NiceToHave` Docker 의 실행 옵션 (cpu, memory) 등을 조정하면서 애플리케이션을 실행하여 성능 테스트를 진행해보면서 적절한 배포 스펙 고려도 한번 진행해보세요!
- 위 테스트를 진행하며 획득한 다양한 성능 지표를 분석 및 시스템 내의 병목을 탐색 및 개선해보고 **(가상)** 장애 대응 문서를 작성하고 제출
- 최종 발표 자료 작성 및 제출

## 📑목차
1. [3주차 과제](#-3주차-과제)
2. [마일스톤](#-마일스톤)
3. [시퀀스 다이어그램](#-시퀀스-다이어그램)
4. [ERD 다이어그램](#-erd-다이어그램)
5. [API 명세서](#-api-명세서)
6. [Swagger](#-swagger)

<br />

## 📌 3주차 과제
<details>
<summary>
  자세히
</summary>  

### Description

- **`콘서트 예약 서비스`**를 구현해 봅니다.
- 대기열 시스템을 구축하고, 예약 서비스는 작업가능한 유저만 수행할 수 있도록 해야합니다.
- 사용자는 좌석예약 시에 미리 충전한 잔액을 이용합니다.
- 좌석 예약 요청시에, 결제가 이루어지지 않더라도 일정 시간동안 다른 유저가 해당 좌석에 접근할 수 없도록 합니다.

### Requirements

- 아래 5가지 API 를 구현합니다.
    - 유저 토큰 발급 API
    - 예약 가능 날짜 / 좌석 API
    - 좌석 예약 요청 API
    - 잔액 충전 / 조회 API
    - 결제 API
- 각 기능 및 제약사항에 대해 단위 테스트를 반드시 하나 이상 작성하도록 합니다.
- 다수의 인스턴스로 어플리케이션이 동작하더라도 기능에 문제가 없도록 작성하도록 합니다.
- 동시성 이슈를 고려하여 구현합니다.
- 대기열 개념을 고려해 구현합니다.

### API Specs

1️⃣ **`주요` 유저 대기열 토큰 기능**

- 서비스를 이용할 토큰을 발급받는 API를 작성합니다.
- 토큰은 유저의 UUID 와 해당 유저의 대기열을 관리할 수 있는 정보 ( 대기 순서 or 잔여 시간 등 ) 를 포함합니다.
- 이후 모든 API 는 위 토큰을 이용해 대기열 검증을 통과해야 이용 가능합니다.

> 기본적으로 폴링으로 본인의 대기열을 확인한다고 가정하며, 다른 방안 또한 고려해보고 구현해 볼 수 있습니다.
> 

**2️⃣ `기본` 예약 가능 날짜 / 좌석 API**

- 예약가능한 날짜와 해당 날짜의 좌석을 조회하는 API 를 각각 작성합니다.
- 예약 가능한 날짜 목록을 조회할 수 있습니다.
- 날짜 정보를 입력받아 예약가능한 좌석정보를 조회할 수 있습니다.

> 좌석 정보는 1 ~ 50 까지의 좌석번호로 관리됩니다.
> 

3️⃣ **`주요` 좌석 예약 요청 API**

- 날짜와 좌석 정보를 입력받아 좌석을 예약 처리하는 API 를 작성합니다.
- 좌석 예약과 동시에 해당 좌석은 그 유저에게 약 5분간 임시 배정됩니다. ( 시간은 정책에 따라 자율적으로 정의합니다. )
- 만약 배정 시간 내에 결제가 완료되지 않는다면 좌석에 대한 임시 배정은 해제되어야 하며 다른 사용자는 예약할 수 없어야 한다.

4️⃣ **`기본`**  **잔액 충전 / 조회 API**

- 결제에 사용될 금액을 API 를 통해 충전하는 API 를 작성합니다.
- 사용자 식별자 및 충전할 금액을 받아 잔액을 충전합니다.
- 사용자 식별자를 통해 해당 사용자의 잔액을 조회합니다.

5️⃣ **`주요` 결제 API**

- 결제 처리하고 결제 내역을 생성하는 API 를 작성합니다.
- 결제가 완료되면 해당 좌석의 소유권을 유저에게 배정하고 대기열 토큰을 만료시킵니다.

<aside>
💡 **KEY POINT**

</aside>

- 유저간 대기열을 요청 순서대로 정확하게 제공할 방법을 고민해 봅니다.
- 동시에 여러 사용자가 예약 요청을 했을 때, 좌석이 중복으로 배정 가능하지 않도록 합니다.
</details>
<br />

## 📌 마일스톤
🔗 [링크](https://github.com/users/kiya-moon/projects/2/views/1?sortedBy%5Bdirection%5D=asc&sortedBy%5BcolumnId%5D=118995284) 

<br />

## 📌 시퀀스 다이어그램
🔗 [링크](https://www.notion.so/6b6edff7e7504a32961f74d7c83465c3?pvs=4)

<br />

## 📌 ERD 다이어그램
<details>
  <summary>
    자세히
  </summary>
  <br />

![image](https://github.com/kiya-moon/hanghaePlus_2024/assets/101784768/29796fba-bec1-4689-a3ec-ceb8fb03e85c)

### CUSTOMER DOMAIN
#### 1. CUSTOMER
- 속성
  - id(PK) : 고객 아이디
  - user_name : 고객 이름
  - balance : 고객 잔액
- 고객 정보를 담는 테이블
<br />

### TOKEN DOMAIN
#### 1. TOKEN
- 속성
  - id(PK) : 토큰 아이디
  - user_id(FK) : 고객 아이디
  - token : 유저 토큰
  - status : 토큰 상태
  - created_at : 토큰 발급 시간
  - expires_at : 토큰 만료 시간
- 대기열을 관리하는 토큰 테이블
- 토큰은 RandomUUID + / + ConcertId로 구성하여 콘서트별로 구분이 가능하도록 할 예정
- 대기, 활성화, 만료로 상태가 구분된다
- 토큰 만료 시간은 토큰 발급 시간으로부터 5분 뒤가 설정된다   
<br />

### CONCERT DOMAIN
#### 1. CONCERT
- 속성
  - id(PK) : 콘서트 아이디
  - name : 콘서트 이름
- 콘서트 기본 테이블
<br />

#### 2. CONCERT_OPTION
- 속성
  - id(PK) : 콘서트 옵션 아이디
  - concert_id(FK) : 콘서트 아이디
  - concert_date : 콘서트 날짜
  - price : 콘서트 가격
- 콘서트 시간별 옵션 테이블
- 동일 콘서트가 시간대별로 들어올 수 있기 때문에 1:N 관계
- (가격 seat 테이블로 옮길 예정. 현재는 좌석에 차등이 없으나 보통 콘서트는 좌석별로 가격이 다르기 때무네...)
<br />

#### 3. SEAT
- 속성
  - id(PK) : 좌석 아이디
  - concert_option_id(FK) : 콘서트 옵션 아이디
  - seat_number : 좌석 번호
  - status: 좌석 상태
- 좌석 테이블
- 콘서트 옵션 별로 50개의 좌석 정보가 들어가는 테이블(1:N 관계)
<br />

### RESEVATION DOMAIN
#### 1. RESERVATION
- 속성
  - id(PK) : 예약 아이디
  - user_id(FK) : 고객 아이디
  - seat_id(FK) : 좌석 아이디
  - status : 예약 상태
  - created_at : 예약 생성 시간
  - updated_at : 예약 업데이트 시간
- 예약 관리 테이블
- 고객이 좌석을 선택하고 예약 버튼 클릭 시 해당 테이블에 들어온다
- 예약 전, 예약 만료, 결제 완료로 상태가 구분된다
<br />

#### 2. PAYMENT
- 속성
  - id(PK) : 결제 아이디
  - reservation_id(FK) : 예약 아이디
  - amount : 결제된 금액
  - payment_date : 결제일
- 결제 테이블
- 예약 테이블의 예약을 결제 완료 시 해당 테이블에 들어온다.
<br />

</details>
<br />

## 📌 API 명세서
<details>
  <summary>
    자세히
  </summary>
  <br />

#### 1. 유저 토큰 발급 API

- **Endpoint**
  - **URL**: `/api/token`
  - **Method**: `POST`
  - **설명**: 대기열을 위한 유저 토큰 발급 요청

- **Request**
  - **Body**: 

    | 항목   | Type | 설명    | 비고 |
    | ------ | ---- | ------- | ---- |
    | userId | Long | 유저 ID |      |

- **Response**
  - **HTTP Status Codes**: 
    - `200 OK`: 성공
    - `400 Bad Request`: 잘못된 요청
    - `500 Internal Server Error`: 서버 오류

  - **Body**:

    | 항목    | Type   | 설명                                  | 비고 |
    | ------- | ------ | ------------------------------------- | ---- |
    | result  | String | 결과 코드 (200 : 성공 / 그 외 : 실패) |      |
    | message | String | 결과 메시지                           |      |
    | data    | Object | 토큰 데이터                           |      |

  - **data 정보 파라미터**

    | 항목          | Type    | 설명           | 비고 |
    | ------------- | ------- | -------------- | ---- |
    | token         | String  | 대기열 토큰    |      |
    | queuePosition | Integer | 대기열 위치    |      |
    | expiresAt     | String  | 토큰 만료 시간 |      |

  - **응답 예시**

    ```json
    {
        "result": "200",
        "message": "Success",
        "data": {
            "token": "randomUUID/concertId",
            "queuePosition": 1,
            "expiresAt": "2024-07-04T12:00:00"
        }
    }
    ```

- **Error**
  - **400 Bad Request**: 필수 파라미터 누락 또는 잘못된 데이터 형식
    - **응답 예시**

      ```json
      {
          "result": "400",
          "message": "Missing or invalid userId"
      }
      ```
  - **500 Internal Server Error**: 토큰 발급 중 서버 오류
    - **응답 예시**

      ```json
      {
          "result": "500",
          "message": "Internal server error"
      }
      ```

- **Authorization**: 없음

#### 2. 예약 가능 날짜 / 좌석 API

- **예약 가능 날짜 조회**

  - **Endpoint**
    - **URL**: `/api/{concertId}/available-dates`
    - **Method**: `GET`
    - **설명**: 예약 가능한 날짜를 조회합니다.

  - **Request**
    - **Query Parameters**: 

      | 항목      | Type   | 설명      | 비고 |
      | --------- | ------ | --------- | ---- |
      | token     | String | 유저 토큰 |      |
      | concertId | Long   | 콘서트Id  |      |
    
  - **Response**
    - **HTTP Status Codes**: 
      - `200 OK`: 성공
      - `401 Unauthorized`: 인증 실패
      - `500 Internal Server Error`: 서버 오류

    - **Body**:

      | 항목           | Type           | 설명                         | 비고 |
      | -------------- | -------------- | ---------------------------- | ---- |
      | concertOptionDtos | List\<Object\> | 예약 가능 콘서트 옵션 리스트 |      |

    - **concertOptionDtos 정보 파라미터**

      | 항목            | Type   | 설명           | 비고 |
      | --------------- | ------ | -------------- | ---- |
      | concertOptionId | Long   | 콘서트 옵션 ID |      |
      | concertDate     | String | 콘서트 날짜    |      |

    - **응답 예시**

      ```json
      {
          "concertOptionDtos": [
              {
                  "concertOptionId": 1,
                  "concertDate": "2024-07-04"
              },
              {
                  "concertOptionId": 2,
                  "concertDate": "2024-07-05"
              }
          ]
      }
      ```

  - **Error**
    - **401 Unauthorized**: 유효하지 않은 토큰
      - **응답 예시**

        ```json
        {
            "result": "401",
            "message": "Invalid or expired token"
        }
        ```
    - **500 Internal Server Error**: 서버 오류
      - **응답 예시**

        ```json
        {
            "result": "500",
            "message": "Internal server error"
        }
        ```

  - **Authorization**: 유저 토큰 필요
    - **Authorization Header**:

      ```
      Authorization: Bearer randomUUID
      ```

- **예약 가능 좌석 조회**

  - **Endpoint**
    - **URL**: `/api/{concertOptionId}/available-seatDtos`
    - **Method**: `GET`
    - **설명**: 특정 날짜에 예약 가능한 좌석을 조회합니다.

  - **Request**
    - **Query Parameters**: 

      | 항목            | Type   | 설명           | 비고 |
      | --------------- | ------ | -------------- | ---- |
      | token           | String | 유저 토큰      |      |
      | concertOptionId | Long   | 콘서트 옵션 ID |      |

  - **Response**
    - **HTTP Status Codes**: 
      - `200 OK`: 성공
      - `401 Unauthorized`: 인증 실패
      - `500 Internal Server Error`: 서버 오류

    - **Body**:

      | 항목  | Type           | 설명                  | 비고 |
      | ----- | -------------- | --------------------- | ---- |
      | seatDtos | List\<Object\> | 예약 가능 좌석 리스트 |      |

    - **seatDtos 정보 파라미터**

      | 항목       | Type   | 설명      | 비고 |
      | ---------- | ------ | --------- | ---- |
      | seatId     | Long   | 좌석 ID   |      |
      | seatNumber | String | 좌석 번호 |      |
      | status     | String | 좌석 상태 |      |

    - **응답 예시**

      ```json
      {
          "seatDtos": [
              {
                  "seatId": 1,
                  "seatNumber": "A1",
                  "status": "열림"
              },
              {
                  "seatId": 2,
                  "seatNumber": "A2",
                  "status": "열림"
              }
          ]
      }
      ```

  - **Error**
    - **401 Unauthorized**: 유효하지 않은 토큰
      - **응답 예시**

        ```json
        {
            "result": "401",
            "message": "Invalid or expired token"
        }
        ```
    - **500 Internal Server Error**: 서버 오류
      - **응답 예시**

        ```json
        {
            "result": "500",
            "message": "Internal server error"
        }
        ```

  - **Authorization**: 유저 토큰 필요
    - **Authorization Header**:

      ```
      Authorization: Bearer randomUUID
      ```

#### 3. 좌석 예약 요청 API

- **Endpoint**
  - **URL**: `/api/reserve`
  - **Method**: `POST`
  - **설명**: 좌석 예약 요청

- **Request**
  - **Body**:

    | 항목            | Type   | 설명           | 비고 |
    | --------------- | ------ | -------------- | ---- |
    | token           | String | 유저 토큰      |      |
    | concertOptionId | Long   | 콘서트 옵션 ID |      |
    | seatId          | Long   | 좌석 ID        |      |
    | userId          | Long   | 유저 ID        |      |

- **Response**
  - **HTTP Status Codes**: 
    - `200 OK`: 성공
    - `401 Unauthorized`: 인증 실패
    - `400 Bad Request`: 잘못된 요청
    - `500 Internal Server Error`: 서버 오류

  - **Body**:

    | 항목    | Type   | 설명                                  | 비고 |
    | ------- | ------ | ------------------------------------- | ---- |
    | result  | String | 결과 코드 (200 : 성공 / 그 외 : 실패) |      |
    | message | String | 결과 메시지                           |      |
    | data    | Object | 예약 결과 데이터                      |      |

  - **data 정보 파라미터**

    | 항목          | Type | 설명    | 비고 |
    | ------------- | ---- | ------- | ---- |
    | reservationId | Long | 예약 ID |      |

  - **응답 예시**

    ```json
    {
        "result": "200",
        "message": "Success",
        "data": {
            "reservationId": 123
        }
    }
    ```

- **Error**
  - **401 Unauthorized**: 유효하지 않은 토큰
    - **응답 예시**

      ```json
      {
          "result": "401",
          "message": "Invalid or expired token"
      }
      ```
    
  - **400 Bad Request**: 필수 파라미터 누락 또는 잘못된 데이터 형식
    - **응답 예시**
  
      ```json
      {
          "result": "400",
          "message": "Missing or invalid parameters"
      }
      ```
    
  - **500 Internal Server Error**: 서버 오류
    
    - **응답 예시**
  
  ```json
  	{
        "result": "500",
        "message": "Internal server error"
    }
  ```
  
  - **Authorization**: 유저 토큰 필요
    - **Authorization Header**:
  
      ```
      Authorization: Bearer randomUUID
      ```
  
  #### 4. 잔액 충전 / 조회 API
  
  - **잔액 충전**
  
    - **Endpoint**
      - **URL**: `/api/balance/charge`
      - **Method**: `PATCH`
      - **설명**: 유저의 잔액을 충전합니다.
  
    - **Request**
      - **Body**:
  
        | 항목   | Type | 설명      | 비고 |
        | ------ |  | --------- | ---- |
        | userId | Long | 유저 ID   |      |
        | amount | int | 충전 금액 |      |
  
    - **Response**
      - **HTTP Status Codes**: 
        - `200 OK`: 성공
        - `400 Bad Request`: 잘못된 요청
        - `500 Internal Server Error`: 서버 오류
  
      - **Body**:
  
        | 항목    | Type | 설명      | 비고 |
        | ------- |  | --------- | ---- |
        | balance | int | 현재 잔액 |      |
  
      - **응답 예시**
  
        ```json
        {
            "balance": 5000
        }
        ```
  
    - **Error**
      - **400 Bad Request**: 필수 파라미터 누락 또는 잘못된 데이터 형식
        - **응답 예시**
  
          ```json
          {
              "result": "400",
              "message": "Missing or invalid parameters"
          }
          ```
      - **500 Internal Server Error**: 서버 오류
        - **응답 예시**
  
          ```json
          {
              "result": "500",
              "message": "Internal server error"
          }
          ```
  
    - **Authorization**: 없음
  
  - **잔액 조회**
  
    - **Endpoint**
      - **URL**: `/api/balance`
      - **Method**: `GET`
      - **설명**: 유저의 현재 잔액을 조회합니다.
  
    - **Request**
      - **Query Parameters**:
  
        | 항목   | Type | 설명    | 비고 |
        | ------ | ---- | ------- | ---- |
        | userId | Long | 유저 ID |      |
  
    - **Response**
      - **HTTP Status Codes**: 
        - `200 OK`: 성공
        - `400 Bad Request`: 잘못된 요청
        - `500 Internal Server Error`: 서버 오류
  
      - **Body**:
  
        | 항목    | Type | 설명      | 비고 |
        | ------- |  | --------- | ---- |
        | balance | int | 현재 잔액 |      |
  
      - **응답 예시**
  
        ```json
        {
            "balance": 5000
        }
        ```
  
    - **Error**
      - **400 Bad Request**: 필수 파라미터 누락 또는 잘못된 데이터 형식
        - **응답 예시**
  
          ```json
          {
              "result": "400",
              "message": "Missing or invalid parameters"
          }
          ```
      - **500 Internal Server Error**: 서버 오류
        - **응답 예시**
  
          ```json
          {
              "result": "500",
              "message": "Internal server error"
          }
          ```
  
    - **Authorization**: 없음
  
  #### 5. 결제 API
  
  - **Endpoint**
    - **URL**: `/api/pay`
    - **Method**: `POST`
    - **설명**: 결제 요청
  
  - **Request**
    - **Body**:
  
      | 항목          | Type | 설명      | 비고 |
      | ------------- |  | --------- | ---- |
      | token         | String | 유저 토큰 |      |
      | reservationId | Long | 예약 ID   |      |
      | amount        | int | 결제 금액 |      |
  
  - **Response**
    - **HTTP Status Codes**: 
      - `200 OK`: 성공
      - `401 Unauthorized`: 인증 실패
      - `400 Bad Request`: 잘못된 요청
      - `500 Internal Server Error`: 서버 오류
  
    - **Body**:
  
      | 항목    | Type   | 설명                                  | 비고 |
      | ------- | ------ | ------------------------------------- | ---- |
      | result  | String | 결과 코드 (200 : 성공 / 그 외 : 실패) |      |
      | message | String | 결과 메시지                           |      |
      | data    | Object | 결제 결과 데이터                      |      |
  
    - **data 정보 파라미터**
  
      | 항목      | Type | 설명    | 비고 |
      | --------- | ---- | ------- | ---- |
      | paymentId | Long | 결제 ID |      |
  
    - **응답 예시**
  
      ```json
      {
          "result": "200",
          "message": "Success",
          "data": {
              "paymentId": 456
          }
      }
      ```
  
  - **Error**
    - **401 Unauthorized**: 유효하지 않은 토큰
      - **응답 예시**
  
        ```json
        {
            "result": "401",
            "message": "Invalid or expired token"
        }
        ```
    - **400 Bad Request**: 필수 파라미터 누락 또는 잘못된 데이터 형식
      - **응답 예시**
  
        ```json
        {
            "result": "400",
            "message": "Missing or invalid parameters"
        }
        ```
    - **500 Internal Server Error**: 서버 오류
      - **응답 예시**
  
        ```json
        {
            "result": "500",
            "message": "Internal server error"
        }
        ```
  
  - **Authorization**: 유저 토큰 필요
    - **Authorization Header**:
  
      ```
      Authorization: Bearer randomUUID
      ```
</details>
<br />
<br />   

## 📌 Swagger

[concert-ticketing-swagger.pdf](https://github.com/user-attachments/files/16186285/concert-ticketing-swagger.pdf)

<br />

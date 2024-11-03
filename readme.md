## 💻 Ably 사전과제 프로젝트

> 시간이 없어 readme를 깔끔하게 정리하지 못했습니다ㅠ 좀 더 편하게 보시려면 노션페이지를 봐주세요!  
https://vanilla-mint-288.notion.site/ABLY-1311473486ff806c8741e081631edf35

#### 실행 방법

> cd ably_backend  # 프로젝트 최상위 루트
> docker-compose up  
> http://localhost:8080/swagger-ui/index.html 
  
- 고려사항
    - 사전과제 실행환경에 docker가 설치되어 있나?
        - docker-compose로 일단 만들고 시간 되면 home server 환경도 만들기
    - docker compose 이미지
        - jdk 17
        - mysql 8.0
        - redis
        - ~~nginx~~
    - 인증/인가
        - JWT 사용, refresh token redis 저장
    - 테스트 코드
        - 비즈니스로직 통합테스트 우선적 작성
        - 이후 시간 남으면 api단위테스트 및 filter 단위 테스트 작성
    - 분산환경 고려
        - 실제로 여러 애플리케이션을 띄우진 못하더라도 고려해서 코딩하기
    - 대용량 처리 고려
        - 동시성 및 성능 고려하여 작성
    - API 명세 작성
        - 수기작성 후 Swagger 적용

## 구현 요구사항

---

- 유저
    - [x]  이메일과 비밀번호로 회원가입 및 로그인을 할 수 있습니다.
        - 고려 사항
            - 이메일 유효성 체크?

              → Spring validation regex로 처리

            - 비밀번호 암호화?

              → Spring BCryptPasswordEncorder로 처리

    - [x]  로그인 후 내 정보를 볼 수 있습니다.
        - 고려 사항
            - 가져올 정보의 종류?
                - 이메일, 이름, 가입일?
                - 찜 리스트나 서랍 리스트는 아래서 구현하므로 굳이
                - 시간 남으면 프로필사진 or 멤버십 포인트 등..
- 찜 서랍
    - [x]  내 찜 서랍을 생성 및 삭제할 수 있습니다.
        - [x]  이미 있는 내 찜 서랍의 이름으로 생성할 수 없습니다.
        - 고려 사항
            - 찜 서랍 삭제시 찜 상품들 CASECADE 삭제처리
    - [x]  내 찜 서랍 목록을 볼 수 있습니다. (페이지네이션 or 무한스크롤)
        - 고려 사항
            - 찜서랍ID, 서랍명만 가져오면 될까?
                - 시간되면 대표 썸네일 1~4개 정도 같이 가져오기
                - + 서랍 내의 찜상품 카운트도 가져오기
- 찜
    - [x]  상품을 찜하거나 해제를 할 수 있습니다.
        - [x]  찜한 상품이 내 다른 찜 서랍에 있을경우 찜할 수 없습니다.
        - [x]  찜 서랍이 하나도 없을 경우 상품을 찜 할 수 없습니다.
        - 고려 사항
            - 사용자가 찜할 서랍을 지정을 하는 건지 안 하는 건지 애매하다..
            - userId, productId 필수로 받고 drawerId 선택으로 받는 걸로
                - drawerId 없으면 존재하는 서랍중 제일 오래된 거에 찜
    - [x]  내 찜 서랍의 찜 목록을 볼 수 있습니다.(페이지네이션 or 무한스크롤)

## DB 설계

---

- USER_INFO


    | 필드명 | 타입 | 제약 |
    | --- | --- | --- |
    | ID | INT | PK, AUTO_INCREMENT |
    | EMAIL | VARCHAR(100) | UNIQUE, NOT NULL |
    | PASSWORD | VARCHAR(16) | NOT NULL |
    | NAME | VARCHAR(30) | NOT NULL |
    | CREATEAD_AT | DATETIME |  |
    - EMAIL PK를 하지 않는이유는, USER PK는 참조가 자주 될 가능성이 높은 필드인데, STRING비교시 INT보다 다소 성능이 떨어질 것 같아서.
- DIBS_DRAWER


    | 필드명 | 타입 | 제약 |
    | --- | --- | --- |
    | ID | INT | PK |
    | USER_ID | INT | FK, UNIQUE(USER_ID, NAME), NOT NULL |
    | NAME | VARCHAR(30) | UNIQUE(USER_ID, NAME), NOT NULL |
    | CREATED_AT | DATETIME |  |
    - USER_ID, NAME 복합키 설정을 하면 DIBS FK 설정 및 API 설계시 참조 파라미터가 많아져서 다소 복잡해짐.
    - USER_ID 인덱스 설정
- DIBS


    | 필드명 | 타입 | 제약 |
    | --- | --- | --- |
    | ID | INT | PK, AUTO_INCREMENT |
    | USER_ID | INT | FK, UNIQUE(USER_ID, PRODUCT_ID), NOT NULL |
    | PRODUCT_ID | INT | FK, UNIQUE(USER_ID, PRODUCT_ID),NOT NULL |
    | DRAWER_ID | INT | FK, NOT NULL |
    | CREATED_AT | DATETIME |  |
    - USER_ID, RPODUCT_ID 인덱스 설정
        - 서랍의 찜 목록 조회쿼리 → USER_ID, PRODUCT_ID 인덱스 후 DRAWER_ID로 필터링
        - 중복 찜 금지로 인해 삽입 삭제시마다 USER_ID, PRODUCT_ID 조건 조회 발생
        - 복합키 설정으로 클러스터 인덱스타게 해도 되지만, 찜은 삽입 삭제가 꽤 빈번할 것 같아 효율이 떨어질 것 같다
    - DRAWER_ID 인덱스는?
        - 유저별 서랍 개수가 그렇게 많지는 않을 것 같아서 안해도 큰 차이 없을 걸로 예상됨
- PRODUCT


    | 필드명 | 타입 | 제약 |
    | --- | --- | --- |
    | ID | INT | PK, AUTO_INCREMENT |
    | NAME | VARCHAR(50) |  |
    | THUMBNAIL | VARCHAR(300) |  |
    | PRICE | INT |  |
    - 더미로 제공해주는 데이터만 넣을 예정

## API 명세

---

### 유저

- 회원가입

    ```json
    [REQUEST]
    POST /v1/user/signup
    {
    	"email": {email},
    	"password": {password},
    	"name": {name}
    }
    
    [RESPONSE]
    {
    	"code" : "{code}",
    	"message" : "{message}",
    	"timestamp" : "{timestamp}"
    }
    ```

- 로그인

    ```json
    [REQUEST]
    POST /v1/user/login
    {
    	"email": {email},
    	"password": {password}
    }
    
    [RESPONSE]
    {
    	"code" : "{code}",
    	"message" : "{message}",
    	"timestamp" : "{timestamp}",
    	"data" : {
    		"id" : "{id}"
    	}
    }
    ```

- 내 정보

    ```json
    [REQUEST]
    GET /v1/user/info/{userId}
    
    [RESPONSE]
    {
    	"code" : "{code}",
    	"message" : "{message}",
    	"timestamp" : "{timestamp}",
    	"data" : {
    		"email": "{email}",
    		"name" : "{name}",
    		"joinDate" : "{joinDate}"
    	}
    }
    ```


### 찜 서랍

- 찜 서랍 생성

    ```json
    [REQUEST]
    POST /v1/dibs-drawer/create
    {
    	"userId": "{userId}"
    	"drawerName": "{drawerName}"
    }
    
    [RESPONSE]
    {
    	"code" : "{code}",
    	"message" : "{message}",
    	"timestamp" : "{timestamp}",
    	"data" : {
    		"userId" : "{userId}",
    		"drawerId" : "{drawerId}",
    		"drawerName" : "{drawerName}"
    	}
    }
    ```

- 찜 서랍 삭제

    ```json
    [REQUEST]
    DELETE /v1/dibs-drawer/{drawerId}
    
    [RESPONSE]
    {
    {
    	"code" : "{code}",
    	"message" : "{message}",
    	"timestamp" : "{timestamp}"
    }
    ```

- 찜 서랍 조회

    ```json
    [REQUEST]
    GET /v1/dibs-drawer/{userId}?page={page}&size={size}
    
    [RESPONSE]
    {
    	"code" : "{code}",
    	"message" : "{message}",
    	"timestamp" : "{timestamp}",
    	"data" : {
    		"content" : {
    			"id" : "{id}",
    			"drawerName" : "{drawerName}",
    			"createdAt" : "{createdAt}"
    		},
    		"pageable" : ...
    	}
    }
    ```


### 찜

- 상품 찜

    ```json
    [REQUEST]
    POST /v1/dibs/add
    {
    	"userId": "{userId}",
    	"productId": "{productId}",
    	"drawerId": "{drawerId}"
    }
    
    [RESPONSE]
    {
    	"code" : "{code}",
    	"message" : "{message}",
    	"timestamp" : "{timestamp}",
    	"data" : {
    		"id" : "{id}"
    	}
    }
    ```

- 상품 찜 해제

    ```json
    [REQUEST]
    DELETE /v1/dibs/{userId}/{productId}
    
    [RESPONSE]
    {
    	"code" : "{code}",
    	"message" : "{message}",
    	"timestamp" : "{timestamp}"
    }
    ```

- 찜 서랍 찜 목록 조회

    ```json
    [REQUEST]
    GET /v1/dibs/{drawerId}?page={page}&size={size}
    
    [RESPONSE]
    {
    	"code" : "{code}",
    	"message" : "{message}",
    	"timestamp" : "{timestamp}",
    	"data" : {
    		"content" : {
    			"dibsId" : "{dibsId}",
    			"productId" : "{productId}"
    		},
    		"pageable" : ...
    	}
    }
    ```

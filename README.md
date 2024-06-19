# SHOPPING-MALL-PROJECT

직관적이고 깔끔한 UI를 가진, 자바와 스프링부트 기반의 상품 주문 서비스입니다. 직관적인 UI를 사용해서 중장년층과 어르신, 아이들도 손쉽게 서비스를 이용할 수 있습니다. 강의에서 배운 내용(검증, 쿠키, 세션, 필터, 예외 처리, 트랜잭션 등)을 프로젝트에 모두 적용해서 응용해보자는 목적을 가지고 제작하게 되었습니다.

<br>

### 사용 기술
* java 11
* springboot 2.7.10
* thymeleaf
* bootstrap 5.2.3, HTML, CSS, javascript
* JDBC
* gradle
* MySQL 8.0.28
* AWS EC2, RDS(DB 엔진은 MariaDB)
<br>

### ⚙️ 모듈
* 회원(member)
* 상품(item)
* 주문(order)
* 장바구니(cart)
<br>

서비스 대상은 관리자(admin), 회원(로그인 상태), 비회원(로그아웃 상태)으로 나뉩니다.
* 비회원은 회원 모듈 중 회원 정보 수정, 회원 탈퇴를 제외한 기능, 상품 모듈 중 상품 조회 기능만 사용할 수 있습니다.
* 회원은 추가적으로 회원 모듈의 모든 기능, 주문 모듈, 장바구니 모듈까지 사용할 수 있습니다.
* 관리자는 모든 모듈에 접근할 수 있습니다.

<br>

### 📄 페이지
1. 회원 가입 페이지
2. 로그인 페이지
3. 회원 정보 수정 페이지
4. 상품 등록 페이지
5. 상품 수정 페이지
6. 관리자 상품 조회 페이지
7. 메인 페이지<br>
   - 상품 목록 나열 - 3가지: 최신순(= 상품 등록 순, 기본 정렬 기준), 가격낮은순, 가격높은순<br>
   - 상품 검색 기능 - 상품명 기준 검색
8. 상품 상세 페이지
9. 장바구니 페이지
10. 주문서 페이지
11. 주문 완료 페이지(주문 완료 후에 나타나는 페이지)
12. 주문 이력 페이지
<br>

### 🔧 ERD 설계
![](https://velog.velcdn.com/images/jiijo86/post/ed4b1ca7-84c0-475c-9a3f-47297b242852/image.png)

<hr>

### 구현 프로젝트
(기능 구현에 초점을 두고 개발하였기에 UI가 많이 단순합니다)
![image](https://github.com/gitgetgot3017/shopping-mall-project/assets/77274460/089cecbb-7b2f-444f-b4aa-472bd63f4e4f)
![image](https://github.com/gitgetgot3017/shopping-mall-project/assets/77274460/54bbb011-599a-44ac-8282-c893e2f96a02)
![image](https://github.com/gitgetgot3017/shopping-mall-project/assets/77274460/4670e8cb-0c09-471d-a954-b356771c4a8a)
![image](https://github.com/gitgetgot3017/shopping-mall-project/assets/77274460/8145e476-5bde-45ae-932f-01c75b2aa9dc)
![image](https://github.com/gitgetgot3017/shopping-mall-project/assets/77274460/214d6ffb-ef64-4456-98f2-c905c90ec4d2)
![image](https://github.com/gitgetgot3017/shopping-mall-project/assets/77274460/8a333192-3c07-4755-a834-b2b8be20bebe)
![image](https://github.com/gitgetgot3017/shopping-mall-project/assets/77274460/f083f965-ae53-4933-94e7-e44a744cdf5c)
![image](https://github.com/gitgetgot3017/shopping-mall-project/assets/77274460/99c3c04f-a31f-411b-8d66-0fb513add0ed)
![image](https://github.com/gitgetgot3017/shopping-mall-project/assets/77274460/1d51b135-4212-4270-bf7c-39b5c42df5c3)
![image](https://github.com/gitgetgot3017/shopping-mall-project/assets/77274460/136c65be-fc25-4411-96b1-517d70a03182)
![image](https://github.com/gitgetgot3017/shopping-mall-project/assets/77274460/8021a11b-c1e5-4199-b62c-dfafbd6e0e3d)
![image](https://github.com/gitgetgot3017/shopping-mall-project/assets/77274460/b0c5d719-4b7a-42c4-b60b-802f8101c7e6)

<br>
<hr>

### 프로젝트 구현 방법 및 트러블슈팅
https://velog.io/@jiijo86/series/%EA%B0%9C%EC%9D%B8-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B81-%EC%87%BC%ED%95%91%EB%AA%B0-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8

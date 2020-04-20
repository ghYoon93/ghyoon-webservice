### 03장 스프링 부트에서 JPA로 데이터베이스 다뤄보자

------

### JPA를 프로젝트에 적용

* ***MyBatis***
  * SQL Mapper로 쿼리를 매핑
  * ORM이 아니다. ORM(Object Relational Mapping)은 객체를 매핑

[TOC]

###### 3.1 JPA 소개

* ***JPA가 나오게 된 배경***
  * 객체를 관계형 데이터베이스에서 관리하는 것은 중요
  * 관계형 데이터베이스를 사용해야만 하는 상황에서 SQL은 피할 수 없다. -> 테이블의 몇 배의 SQL을 만들고 유지보수하는 단순 반복 작업의 문제
  * 관계형 데이터베이스와 객체지향 프로그래밍 언어는 패러다임의 불일치가 있다.
  * 관계형 데이터베이스는 어떻게 데이터를 저장할지에 초점이 맞춰진 기술이고 객체지향 프로그래밍 언어는 메세지를 기반으로 기능과 속성을 한 곳에서 관리하는 기술이다.



* ***JPA***
  * 서로 지향하는 바가 다른 2개 영역(객체지향 프로그래밍 언어와 관계형 데이터베이스)을 중간에서 패러다임 일치를 시켜주기 위한 기술
  * 개발자는 객체지향적으로 프로그래밍을 하고, JPA가 이를 관계형 데이터베이스에 맞게 SQL을 대신 생성해서 실행



* ***Spring Data JPA***
  * JPA는 인터페이스로서 자바 표준명세서. 인터페이스인 JPA를 사용하기 위해서는 구현체가 필요
  * 구현체에는 대표적으로 Hibernate, Eclipse Link등이 있다.
  * Spring에서 JPA를 사용할 때는 이 구현체를 직접 다루진 않는다.
  * Spring Data JPA는 구현체들을 좀 더 쉽게 사용하고자 추상화한 모듈
    * JPA <- Hibernate <- Spring Data JPA
  * 구현체(Hibernate, Eclipse Link...) 교체의 용이성과 저장소(MongoDB, MaraiDB...) 교체의 용이성이 있다.
  * Spring Data의 하위 프로젝트들은 save(), findAll(), findOne()등을 인터페이스로 갖고 있다.



* ***실무에서 JPA***
  * 실무에서 JPA를 사용하지 못하는 가장 큰 이유로 높은 러닝 커브가 있다.
  * JPA를 잘 쓰려면 객체지향 프로그래밍과 관계형 데이터베이스를 둘 다 이해해야한다.
  * JPA를 사용하면 CRUD쿼리를 직접 작성할 필요가 없다.
  * 또한, 부모-자식 관계 표현, 1:N 관계 표현, 상태와 행위를 한 곳에서 관리하는 등 객체지향 프로그래밍을 쉽게 할 수 있다.



* ***요구사항 분석***
  * 게시판 기능
    * 게시글 조회
    * 게시글 등록
    * 게시글 수정
    * 게시글 삭제
  * 회원 기능
    * 구글/네이버 로그인
    * 로그인한 사용자 글 작성 권한
    * 본인 작성 글에 대한 권한 권리





###### 3.2 프로젝트에 Spring Data Jpa 적용하기

* ***build.gradle***
  * `spring-boot-starter-data-jpa`
    * 스프링 부트용 Spring Data Jpa 추상화 라이브러리
    * 스프링 부트 버전에 맞춰 자동으로 JPA관련 라이브러리들의 버전을 관리해준다.
  * `h2`
    * 인메모리 관계형 데이터베이스
    * 별도의 설치가 필요 없이 프로젝트 의존성만으로 관리할 수 있다.
    * 메모리에서 실행되기 때문에 애플리케이션을 재시작할 때마다 초기화된다는 점을 이용하여 테스트 용도로 많이 사용된다.
    * JPA의 테스트, 로컬 환경에서의 구동에서 사용



* `com.ghyoon.webservice.practice2.domain`
  * 도메인을 담을 패키지



* ***domain***
  * 게시글, 댓글, 회원, 정산, 결제 등 ***소프트웨어에 대한 요구사항 혹은 문제 영역***
  * xml에 쿼리를 담고, 클래스는 오로지 쿼리의 결과만 담던 일들이 모두 도메인 클래스에서 해결된다.



* ***Posts.class***
  * 실제 DB의 테이블과 매칭될 클래스, Entity 클래스라고도 한다.
  * DB 데이터에 작업할 경우 실제 쿼리를 날리기보다는, Entity 클래스의 수정을 통해 작업한다.
  * 주요 어노테이션은 클래스에 가깝게 둔다.
  * `@Entity`
    * 테이블과 링크될 클래스임을 나타낸다.
    * 기본값으로 클래스의 카멜케이스 이름을 언더스코어 네이밍(_)으로 테이블 이름을 매칭
    * ex)SalesManager.class -> sales_manager table
  * `@Id`
    * 해당 테이블의 PK필드를 나타낸다.
  * `@GeneratedValue`
    * PK의 생성 규칙을 나타낸다.
    * 스프링 부트 2.0에서는 `GeneratrionType.IDENTITY` 옵션을 추가해야만 auto_increment가 된다.
  * `@Column`
    * 테이블의 칼럼을 나타내며 굳이 선언하지 않더라도 ***해당 클래스의 필드는 모두 컬럼***이 된다.
    * 기본값 외에 추가로 변경이 필요한 옵션이 있을 때 사용한다.
    * 문자열의 경우 VARCHAR(255)가 기본값인데, 사이즈를 500(title)으로 늘리고 싶거나 타입을 TEXT로 변경하고 싶거나(content)등의 경우에 사용된다.
  * `@NoArgsConstructor`
    * 기본 생성자 자동 추가
    * `public Post(){}`와 같은 효과
  * `@Getter`
    * 클래스 내 모든 필드의 Getter메소드를 자동생성
  * `@Builder`
    * 해당 클래스의 빌더 패턴 클래스를 생성
    * 생성자 상단에 선언 시 생정자에 포함된 필드만 빌더에 포함



* ***`@Setter`가 없는 이유***

  * getter/setter를 무작성 생성하면 해당 클래스의 인스턴스 값들이 언제 어디서 변해햐 하는지 코드상으로 명확하게 구분할 수 없다.

  * 이로 인해 차후 기능 변경 시 복잡해진다.

  * 따라서 Entity 클래스에서는 절대 Setter 메소드를 만들지 않는다.

  * 해당 필드의 값 변경이 필요하면 ***명확히 그 목적과 의도를 나타낼 수 있는 메소드***를 추가해야한다.

    

* ***Setter가 없는 상황에서 값을 채워 DB에 삽입***

  * 기본적인 구조는 생성자를 통해 최종값을 채운 후 DB에 삽입하는 것이며, 값 변경이 필요한 경우 해당 이벤트에 맞는 public 메소드를 호출하여 변경하는 것을 전제로 한다.
  * 이번에는 생성자 대신에 `@Builder`를 통해 제공되는 빌더 클래스를 사용한다.
  * 생성자나 빌더나 생성 시점에 값을 채워주는 역할은 똑같다.
  * 다만, 생성자의 경우 ***지금 채워야 할 필드가 무엇인지 명확히 지정할 수가 없다.***
  * 생성자에 값을 채울 때 파라미터의 위치가 변경되도 코드를 실행하기 전까지는 문제를 찾을 수 없다.
  * 빌더를 사용하게 되면 어느 필드에 어떤 값을 채워야할지 명확하게 인지할 수 있다.



* ***PostsRepository.class***
  * Posts 클래스로 Database를 접근하게 해줄 JpaRepository
  * MyBatis에서 Dao라 불리는 DB Layer 접근자
  * 인터페이스로 생성하며 `JpaRepository<Entity 클래스, PK 타입>`를 상속하면 기본적인 CRUD메소드가 자동으로 생성된다.
  * Entity 클래스와 기본 Entity Repository는 함께 위치해야한다.
  * 둘은 아주 밀접한 관계이고, Entity 클래스는 기본 Repository 없이는 제대로 역할을 할 수 없다.





###### 3.3 Spring Data JPA 테스트 코드 작성하기

* **PostsRepositoryTest.class**

  * `@After`

    * Junit에서단위 테스트가 끝날 때마다 수행되는 메소드를 지정
    * 보통은 배포 전 테스트를 수행할 때 테스트 간 데이터 침범을 막기 위해 사용
    * 여러 테스트가 동시에 수행되면 테스트용 데이터베이스인 H2에 데이터가 그대로 남아 있어 다음 테스트 실행 시 테스트가 실패할 수 있다.

  * `postsRepository.save()`

    * 테이블 posts에 insert/update 쿼리를 실행
    * id 값이 있다면 update가, 없다면 insert 쿼리가 실행

  * `postsRepository.findAll()`

    * 테이블 posts에 있는 모든 데이터를 조회해오는 메소드

      

* ***resources/application.properties***
  * application의 설정을 담당하는 파일



* ***실제로 실행된 쿼리를 로그로 확인***
* `spring.jpa.Show_sql=true`로 실행된 쿼리를 로그로 볼 수 있다.
* `spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5InnoDBDialect`로 출력되는 쿼리 로그를 MySQL 버전으로 볼 수 있따.



###### 3.4 등록/수정/조회 API 만들기

* **API를 만들기 위해 필요한 클래스**

  * Request 데이터를 받을 ***Dto***
  * API 요청을 받을 ***Controller***
  * 트랜잭션, 도메인 기능 간의 순서를 보장하는 ***Service***

  

* ***Service는 트랜잭션, 도메인 간 순서 보장의 역할만 한다.***



* ***Web Layer***

  * 흔히 사용하는 컨트롤러(`@Controller`)와 JSP/Freemaker 등의 뷰 템플릿 영역

  * 이외에도 필터(`@Filter`), 인터셉터, 컨트롤러 어드바이스(`@ControllerAdvice`)등 ***외부 요청과 응답***에 대한 전반적인 영역

    

* ***Service Layer***
  * `@Service`에 사용되는 서비스 영역
  * 일반적으로 Controller와 Dao의 중간 영역에서 사용
  * `@Transactional`이 사용되어야하는 영역



* ***Repository Layer***
  * Database와 같이 데이터 저장소에 접근하는 영역
  * Dao(Data Access Object)영역으로 이해하면 쉽다.



* ***Dtos***
  * Dto(Data Transfer Object)는 ***계층 간에 데이터 교환을 위한 객체***
  * Dtos는 이들의 영역
  * 예를 들어 템플릿 엔진에서 사용될 객체나 Repository Layer에서 결과로 넘겨준 객체 등
    
* ***Domain Model***
  * 도메인이라 불리는 개발 대상을 모든 사람이 동일한 관점에서 이해할 수 있고 공유할 수 있도록 단순화시킨 것
  * 이를테면 택시 앱이라고 하면 배차, 탑승, 요금 등이 모두 도메인이 될 수 있다.
  * `@Entity`를 사용해봤다면 `@Entity`가 사용된 영역 역시 도메인 모델이라고 이해하면 된다.
  * 다만, 무조건 데이터베이스의 테이블과 관계가 있어야만 하는 것은 아니다.
  * VO처럼 값 객체들도 이 영역에 해당하기 때문이다.
    
* ***비즈니스 로직***
  * Web, Service, Repository, Dto, Domain, 이 5삭지 레이어에서 비즈니스 처리를 담당하는 곳은 ***Domain***
  * 기존에 서비스로 비즈니스 로직을 처리하던 방식을 트랜잭션 스크립트라고 한다.
  * 이렇게 되면 모든 로직이 서비스 클래스 내부에서 처리된다. 
  * 그러다 보니 서비스 계층이 무의미하며, 객체라는 단순히 데이터 덩어리 역할만 하게 된다.
  * 하지만 비즈니스 로직을 도메인 모델에서 처리할 경우 각자의 도메인이 본인의 이벤트를 처리하며, 서비스 메소드는 트랜잭션과 도메인 간의 순서만 보장해준다.



* ***PostApiController.class / PostsService.class***
  * 스프링에서 Bean을 주입받는 방식들에는 `@Autowired` `setter`, `생성자`가 있다.
  * 이 중 가장 권장하는 방식이 생성자로 주입받는 방식이다.
  * 즉, 생성자로 Bean 객체를 받도록 하면 `@Autowired`와 동일한 효과를 볼 수 있다.



* ***PostsSaveReuestDto***
  * Entity 클래스를 Request/Response 클래스로 사용해서는 안된다.
  * Entity 클래스는 ***데이터베이스와 맞닿는 핵심 클래스***
  * Entity 클래스를 기준으로 테이블이 생성되고, 스키마가 변경된다.
  * 화면 변경은 아주 사소한 기능 변경인데, 이를 위해 테이블과 연결된 Entity 클래스를 변경하는 것은 너무 큰 변경
  * 수많은 서비스 클래스나 비즈니스 로직들이 Entity 클래스를 기준으로 동작한다.
  * Entity 클래스가 변경되면 여러 클래스와에 영향을 끼치지만, Request와 Response용 Dto는 View를 위한 클래스라 정말 자주 변경이 필요
  * View Layer와 DB Layer의 역할 분리를 철저하게 하는 것이 좋다.
  * 실제로 Controller에서 결괏값으로 여러 테이블을 조인해서 줘야 경우가 빈번하므로 Entity 클래스만으로 표현하기가 어려운 경우가 많다.
  * 꼭 Entity 클래스와 Controller에서 쓸 Dto는 분리해서 사용해야 한다.
    
* ***PostsApiControllerTest.class***
  * `@WebMvcTest`의 경우 JPA 기능이 작동하지 않는다. 따라서 JPA 기능까지 한번에 테스트할 때는 `@SpringBootTest`와 `TestRestTemplate`을 사용하면 된다.
  * `WebEnvironment.RANDOM_PORT`: 인한 랜덤 포트 실행



* ***PostsResponseDto.class***
  * Entity의 필드 중 일부만 사용하므로 생성자로 Entity를 받아 필드에 값을 넣는다.
  * 굳이 모든 필드를 가진 생성자가 필요하진 않으므로 Dto는 Entity를 받아 처리
    
* ***PostsUpdateRequestDto.class / PostsUpdateRequestDto.class / Posts.class / PostsService.class***
  * JPA의 영속성 컨텍스트 때문에 update 기능에서 데이터베이스에 쿼리를 날리는 부분이 없다.
  * 영속성 컨텍스트란, 엔티티를 영구 저장하는 환경
  * 일종의 논리적 개념이라고 보면 되며, JPA의 핵심 내용은 엔티티가 영속성 컨텍스트에 포함되어 있냐 아니냐로 갈린다.
  * JPA의 엔티티 매니저가 활성화된 상태로 트랜잭션 안에서 데이터베이스에서 데이터를 가져오면 이 데이터는 영속성 컨텍스트가 유지된 상대
  * 이 상태에서 해당 데이터의 값을 변경하면 트랜잭션이 끝나는 시점에 해당 테이블에 변경분을 반영한다.
  * 즉, Entity 객체의 값만 변경하면 별도로 Update쿼리를 날릴 필요가 없다.
  * 이 개념을 ***더티 체킹***이라 한다.
<img src="https://user-images.githubusercontent.com/83625797/132082312-dee6dc10-4215-4b38-bb6a-086ca922b217.png" width="200" >

# WtiteYourThink(롸쳐띵)  

바쁜 일상 속 장문의 일기를 쓸 시간이 없는 현대인들을 위한 짧고 간결하게 일상을 기록하는 모바일 어플리케이션.



## Technical Lead
* Android Native(JAVA)  
* RecyclerView를 이용한 SQLite 데이터 표시 및 수정, 삭제 기능 구현
* Firebase를 활용한 로그인 인증(Google, Facebook)
* SQLite DB와 Firebase DB 연동
* BottomSheetDialog를 이용한 입력창 구현
* 달력 구현 (GitHub 오픈소스 활용) : 날짜별로 일기 쓴 횟수 보여줌
* Firebase를 활용한 FCM(푸시알림) 기능 구현
* 영문화 작업으로 전 세계 사람들이 이용 가능하도록 구현

## Project Lead  
* 구글 플레이스토어, 갤럭시스토어 동시 출시 및 유지보수
* 간트 차트를 이용한 프로젝트 일정관리 
* GitHub를 통한 개발 프로세스 기록 및 버전관리  

## Problem Solving
* **P1** : Firebase DB만 쓰면 데이터를 읽거나 갱신할 때 딜레이가 생기는 문제
* **Sol1** : Firebase DB와 SQLite DB를 연동시켰다. 따라서 첫 로그인 시에만 Firebase 데이터를 불러와 SQLite DB에 저장하는 과정이 생긴다.  
  이는 처음에 1~2초정도의 대기시간이 있지만 상시 딜레이가 생기는 것 보다는 낫다는 평가를 받음
* **P2** : 갤럭시 스토어 출시 전 심사에서 한국어로만 구성된 앱이라서 반려됨
* **Sol2** :  Android Studio의 Translations Editor를 이용하여 한국이 아닌 지역에서는 영어로 자동 번역되도록 영문화 작업을 진행하였다. 후에 심사는 안정적으로 통과.

## Screen Shots
### -Login  
 <img src="https://user-images.githubusercontent.com/83625797/132082301-10fe3515-a286-42cb-9898-9dbec11414f0.jpg" width = "200">  
 
### -Main  
<img src="https://user-images.githubusercontent.com/83625797/132082302-27c991d9-d95c-438e-b1dc-7d79f75b7631.jpg" width= "200">  

### -스무고개형 음식추천
<img src="https://user-images.githubusercontent.com/83625797/132082304-6fed3a86-242b-4131-9e39-a182b0805ab5.jpg" width = "200">  

### -룰렛형 음식추천
<img src="https://user-images.githubusercontent.com/83625797/132082305-0e242505-9651-4866-b81a-34aca578076e.jpg" width = "200">

### -음식 다이어리
<img src="https://user-images.githubusercontent.com/83625797/132082308-89496c21-cbea-4394-8393-31a0d42ca0ae.jpg" width = "200">

### -주변 음식점 찾기
<img src="https://user-images.githubusercontent.com/83625797/132082309-cc46436e-40e4-46a8-824a-589b637fcc52.jpg" width = "200">

### -음식 사전
<img src="https://user-images.githubusercontent.com/83625797/132082310-c107de4f-fafc-4758-9335-a5bf524bff04.jpg" width = "200">






<img src="https://user-images.githubusercontent.com/83625797/132083273-a7608015-11aa-4adc-92e1-eb49c0359b4b.png" width="200" >

# WtiteYourThink(롸쳐띵)  
Firebase를 활용하여 제작한 일일 일상기록 시스템입니다.

## 🖼️ Screen Shots
| ❤️ 날짜별 탐색| 🖋️ 데이터 추가| ♻️ 데이터 삭제 및 복원| 🗓️ 캘린더|
|---|---|---|---|
|![데이터 필터링](https://user-images.githubusercontent.com/83625797/156904796-c5e0aafb-a57d-47e3-bba1-005b52cd770c.gif)|![추가 기능](https://user-images.githubusercontent.com/83625797/156904798-e9c9ad96-519e-4804-8e12-ca904c9d0acb.gif)|![데이터 삭제 및 복원](https://user-images.githubusercontent.com/83625797/156904797-5167e046-5507-4a58-884e-d9dc2f785ab4.gif)|![캘린더 기능](https://user-images.githubusercontent.com/83625797/156904799-165b4ce8-b21b-44be-8f7a-5db0479ecfc2.gif)|

## 👨‍💻 사용 기술 스택
### Programming Language
* Kotlin(Android)

### Android AAC
  - Navigation
  - ViewModel
  - LiveData
  - View Binding

### Database
 * Firebase Realtime Database

### IDE
* Android Studio Bumbleblee


## 🚧 아키텍쳐
아키텍쳐로 `MVVM(Model-View-ViewModel)`패턴을 사용하였습니다.
![image](https://user-images.githubusercontent.com/83625797/156362905-96f8a59f-9026-4023-85c0-36f6034b966f.png)
* `Activity/Fragment`: 3 액티비티 2프래그먼트구성으로, **네비게이션 컴포넌트를 이용**하여 관리하였습니다.
* `ViewModel`: **LiveData**를 사용하여 **뷰 - 모델간의 결합도를 낮추고 데이터를 동기화**하였습니다. 
* `Model`: **Firebase**을 사용하여 고객 정보와 일기 **데이터를 실시간으로 백업**하였습니다.



## 👨‍💻 주요 기능
* 리사이클러뷰 최적화 및 데이터 필터링 기능 구현 - DiffUtil 사용
* 구글 페이스북 로그인 시스템 - Firebase Auth사용
* 화면 구성 변경시에도 작업하던 데이터 유지 - ViewModel + LiveData 사용
* 일기 사진 첨부 기능 - Firebase Storage 사용
* 캘린더 기능 - CompactCalendarView 외부 라이브러리 사용
* 데이터 복원 및 삭제 기능 - 스낵바, 리사이클러뷰 Swipe 기능 사용
 
## 👱‍♂️ Problem Solving
* [1. MVVM 리팩토링을 시작하다](https://velog.io/@blucky8649/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-MVVM-%EB%A6%AC%ED%8C%A9%ED%86%A0%EB%A7%81-%EB%8F%84%EC%A0%84)
* [2. 리사이클러뷰의 데이터를 날짜별로 필터링해보자](https://velog.io/@blucky8649/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-%EB%A6%AC%EC%82%AC%EC%9D%B4%ED%81%B4%EB%9F%AC%EB%B7%B0-%EB%8D%B0%EC%9D%B4%ED%84%B0-%EA%B2%80%EC%83%89-%ED%95%84%ED%84%B0%EB%A7%81-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0) 
* [3. 스낵바를 사용하여 데이터 삭제 복원 기능을 추가해보자](https://velog.io/@blucky8649/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-%EC%8A%A4%EB%82%B5%EB%B0%94%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%98%EC%97%AC-%EB%8D%B0%EC%9D%B4%ED%84%B0-%EC%82%AD%EC%A0%9C-%EB%B3%B5%EC%9B%90-%EA%B8%B0%EB%8A%A5-%EB%A7%8C%EB%93%A4%EA%B8%B0)
* [4. ViewModel + LiveData 를 활용하여 화면 구성 변경 시에도 UI 데이터를 유지시켜보자](https://velog.io/@blucky8649/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-%ED%99%94%EB%A9%B4-%EB%B3%80%EA%B2%BD-%EC%8B%9C-%EC%95%B1%EC%9D%B4-%EB%A6%AC%ED%94%84%EB%A0%88%EC%89%AC-%EB%90%98%EB%8A%94-%EB%AC%B8%EC%A0%9C-%ED%95%B4%EA%B2%B0)
* [5. DiffUtil을 사용하여 리사이클러뷰를 최적화하고 데이터를 자동으로 갱신시켜보자](https://velog.io/@blucky8649/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-diffUtil%EC%9D%84-%EC%82%AC%EC%9A%A9%ED%95%98%EC%97%AC-%EB%A6%AC%EC%82%AC%EC%9D%B4%ED%81%B4%EB%9F%AC%EB%B7%B0-%EB%8D%B0%EC%9D%B4%ED%84%B0%EB%A5%BC-%EC%9E%90%EB%8F%99%EC%9C%BC%EB%A1%9C-%EA%B0%B1%EC%8B%A0%EC%8B%9C%ED%82%A4%EC%9E%90)







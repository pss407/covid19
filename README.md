# covid19
코로나 감염현황 API를 이용한 안드로이드 앱 토이 프로젝트


## 개요

![앱 메인 화면](https://user-images.githubusercontent.com/44769544/116810305-0a2ecb80-ab7e-11eb-885c-5a3477327e8a.png)

- 앱에 제공되는 정보는 확진환자 수, 검사중인 환자 수, 격리해제된 환자 수, 사망자 수 총 4가지다.
- 당일 기준 4종류의 정보 밑에는 전날과의 차이를 보여주며 증가하면 빨강, 감소하면 파랑으로 표현.
- 해당 정보를 당일 오전 10시 즈음에 업데이트를 해주기 때문에 업데이트가 안된 경우 확인할 수 있도록 기준일 표시
- 기준일 옆에는 새로고침 버튼 제공

## 2021-05-02

- 위젯기능 추가(아직은 확진환자 수만 표시)

![위젯](https://user-images.githubusercontent.com/44769544/116810446-ce483600-ab7e-11eb-8ae6-628eda43abaa.png)

## 2021-05-02

- Retrofit 통신 추가

## 2021-07-12

- UI 개선(천단위 콤마 추가, 색상 변경, 글씨 스타일 변경, 앱 위젯 상승/하락 표시추가 등)

![메인](https://user-images.githubusercontent.com/44769544/125419486-da947f3a-7d9c-4f20-acbb-6c661a4a68ad.png)

![위젯](https://user-images.githubusercontent.com/44769544/125273587-0bcbfb00-e348-11eb-9fca-4b650648471b.png)

### 추가할 기능

- ~~위젯 다듬기(위젯에서 증/감 표시 사라짐)~~
- SQLite나 다른 DB를 이용해서 추가 기능

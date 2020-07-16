# Madcamp - GotiPocari
Android project for week 1 of Madcamp@KAIST


# GotiPocari Application의 구성
**1. Activity (2개)**
   * MainActivity
   * LoadingActivity

**2. Fragment (4개)**
   * Dial Fragment
   * PhoneBook Fragment
   * Gallery Fragment
   * RandomGame Fragment - (In App version / AR version)

**3. Control Flow**
   * Application 구동 시 MainActivity에서 LoadingActivity를 일정시간 실행시켜 로딩화면을 실행합니다.
   ![Loading](https://github.com/geonsikSeo/GotiPocari_Project1/blob/master/imageformd/loadingimage.png)


   * LoadingActivity의 startLoading() 함수에서 시간이 종료되면 MainActivity에서 activity_main.xml을 실행합니다.
   ![main](https://github.com/geonsikSeo/GotiPocari_Project1/blob/master/imageformd/main.png)

   * Navigation Bar를 Tap하여 위 2. Fragment에서 설명한 각 Fragment로 이동이 가능합니다.



# Dial Fragment 구현 사항

**1)기능**

   ![main](https://github.com/geonsikSeo/GotiPocari_Project1/blob/master/imageformd/main.png)

   * 각 Button 클릭시 해당 숫자를 coout라는 변수에 string으로 변환하고, setText함수를 통해 입력된 문자열을 TextView에 출력합니다.

   또한 CallButton과 SmsButton에 대한 OnClickListener를 구현했으며, Call 버튼 클릭시 권한이 허용되었는지 검사하고 Intent를 통해(Intent.Action_View)

   count에 저장된 전화번호로 전화를 걸 수 있습니다. sms또한 권한 허용 여부 검사 후, 해당 번호로 문자를 보낼 수 있습니다.

   권한 검사는 checkSelfPermission()함수를 통해서 진행합니다.

**2) 구현 코드**

```java
 @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backspace) {
            if (count.equals(""))
                return;
            count = count.substring(0, count.length() - 1);
        } else if (v.getId() == R.id.btext) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, PERMISSIONS_REQUEST_SEND_SMS);
            else {
                Intent text = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + count));
                startActivity(text);
            }
        } else if (v.getId() == R.id.bcall) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(getActivity(), new String[]{ Manifest.permission.CALL_PHONE }, PERMISSIONS_CALL_PHONE);
            else {
                Intent call = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+count));
                startActivity(call);
            }
        } else {
            count += Integer.toString(v.getId() - R.id.b0);
        }
        text.setText(count);
    }
```



# PhoneBook Fragment 구현 사항

**1)기능**

Phonebook Fragment를 구현하기 위해 6개의 Class를 사용했습니다.

* ContactRepository

* JsonData (기존에는 Json파일을 파싱하여 만들었으나 연락처를 불러오는 형식으로 데이터를 받아옵니다.)

* PhoneBookFragment

* PhoneBookAdapter

* PhoneBookViewModel

* PhoneBookViewModelFactory


List는 Recycler View를 사용했으며, 각 view를 탭하면 전화와 메세지를 걸 수 있는 Icon이 나옵니다.
이는 expandable setVisibility함수를 통해 구현했고, 해당 Icon을 클릭하면 JsonData Type 정보 중 전화번호를 받아와
Intent로 넘겨 직접 전화와 메세지를 실행할 수 있습니다.

![phonebook](https://github.com/geonsikSeo/GotiPocari_Project1/blob/master/imageformd/Phonebook1.png)

또한 Adapter에 Filter()함수를 구현하였습니다. 이는 Action bar에 구현한 SearchView를 통해 검색어를 substring으로 가지는 목록을
검색해 가져오기 위한 함수입니다.

SearchView는 OnCreateOptionMenu()함수를 Override하여 Action bar에 띄웠고, searchView에 이벤트 처리를 위해
setOnQueryTextListener를 통해 onQueryTextChange(글자가 입력중일 때)에서 Fliter 함수를 호출하였습니다.


글자가 바뀔때마다 초기상태의 리스트를 저장한 backupList라는 변수를 인자로 넣고 필터링 한 후, Recycler view에 띄웁니다.


또한 setOnActionExpandListener를 사용해서 SearchView가 축소되면 기존 전체리스트로 복구하기 위해 backupList를 사용해 adapter의 listViewItemList를 초기화
한 후 값을 넣어줍니다.

데이터는 핸드폰의 주소록에서 값을 가져오는 형식으로 Permission을 처리하고 가져왔습니다.

해당 데이터들은 ContactRepository Class에서 가져옵니다. 데이터를 가져오기 위해 Cursor Class를 사용했으며, 구현한 코드는 2) 구현 코드에 있습니다. PhoneNumber, Email, PhotoUri, id, name을 Fetch하며 Phonenumber, Email, PhotoUri는 id가 필요하기 때문에 따로 fetchPhoneNumber / Email / PhotoUri 함수가 정의되어있습니다.


해당 데이터는 JsonData type으로 선언된 ArrayList에 저장됩니다.


**2) 구현 코드**

* PhoneBookAdapter - Bind 함수

```java
  public void bind(final JsonData item) {
            boolean expanded = item.getExpanded();

            expandableList.setVisibility(expanded ? View.VISIBLE : View.GONE);

            name.setText(item.getName());
            number.setText(item.getNumber());
            email.setText(item.getEmail());
            photo.setImageURI(item.getPhoto());
            if (photo.getDrawable() == null)
                photo.setImageResource(R.drawable.ic_profile_placeholder);

            callButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (ActivityCompat.checkSelfPermission(PhoneBookAdapter.this.context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                        ActivityCompat.requestPermissions((Activity)PhoneBookAdapter.this.context, new String[]{ Manifest.permission.CALL_PHONE }, PhoneBookFragment.PERMISSIONS_CALL_PHONE);
                    else {
                        Intent call = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + item.getNumber()));
                        context.startActivity(call);
                    }
                }
            });

            smsButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (ActivityCompat.checkSelfPermission(PhoneBookAdapter.this.context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
                        ActivityCompat.requestPermissions((Activity)PhoneBookAdapter.this.context, new String[]{ Manifest.permission.SEND_SMS }, PhoneBookFragment.PERMISSIONS_REQUEST_SEND_SMS);
                    else {
                        Intent send = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + item.getNumber()));
                        context.startActivity(send);
                    }
                }
            });
        }
    }
```

* PhoneBookAdapter - fillter 함수

```java
public void fillter(String searchText, ArrayList<JsonData> backupList){

        listViewItemList.clear();

        for( JsonData item : backupList)
        {
            if(item.getName().toUpperCase().contains(searchText.toUpperCase()))
            {
                listViewItemList.add(item);
            }
        }

        notifyDataSetChanged();

    }
```

* ContactRepository - getContactList()

```java
 public ArrayList<JsonData> getContactList() {
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        ArrayList<JsonData> contacts = new ArrayList<JsonData>();

        if (cur == null || cur.getCount() == 0)
            return contacts;

        while (cur != null && cur.moveToNext()) {
            String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String number = fetchPhoneNumber(cr, id);
            String email = fetchEmail(cr, id);
            Uri photo = fetchPhotoUri(cr, id);

            contacts.add(new JsonData(name, number, email, photo));
        }

        if (cur != null)
            cur.close();

        return contacts;
    }
```

# Gallery 구현사항

세번째 탭에는 Gallery 기능을 구현했습니다. 
Gallery의 기능은 다음과 같습니다


*  여러개의 이미지를 3개의 열로 표시
*  클릭하면 이미지를 확대해서 보여주는 기능
*  이미지 밑에 프리뷰 구현

여러개의 이미지를 보여주는 PicturesFragment에는 GridView를 사용했으며, 각 이미지를 클릭하면 FullImageActivity로 이동해 확대된 사진이 보입니다.

FUllImageActivity는 Viewpager과 RecyclerView로 이루어져 있고, Viewpager에는 확대된 이미지, RecyclerView에서는 Preview 이미지가 보입니다.

Viewpager를 이용해 슬라이드 모션을 취하면 다음 이미지로 넘어가고, Preview Image에서 클릭하면 그에 해당하는 Image가 Viewpager에 나타나도록 구현했습니다.

fragment간의 정보는 recyclerView.findViewHolderForAdapterPosition(position)을 이용해 주고 받았고, fragment에서 Activity로 넘어갈때는 Intent 함수를 이용해 정보를 전달했습니다.


![gallery](https://user-images.githubusercontent.com/63027385/87500771-54e76180-c698-11ea-8d0d-413dc63bd879.PNG)
![gallery click](https://user-images.githubusercontent.com/63027385/87500824-7d6f5b80-c698-11ea-9bf2-63a5d03139a8.PNG)




**2)구현코드**

# RandomGame

마지막 탭에서는 [병 돌리기 게임](https://en.wikipedia.org/wiki/Spin_the_bottle)을 구현하였습니다.

해당 Fragment에는 두 가지 종류(2D, AR)의 병 돌리기 게임이 있습니다. Swipe 또는 상단에 있는 Tab으로 두 Fragment간 이동이 가능합니다.

| 병 돌리기 게임 - 2D 버전                             | 병 돌리기 게임 - AR 버전                             |
|------------------------------------------------------|------------------------------------------------------|
| ![Bottle Spin 2D Version](https://i.imgur.com/XOEYxnq.jpg) | ![Bottle Spin AR Version](https://i.imgur.com/JChGl1C.jpg) |

### 병 돌리기 게임 - 2D 버전

2D 버전의 병 돌리기 게임은 버튼을 누를 시 선택된 병이 회전되면서 임의의 각도에서 멈추게 됩니다. `ImageView`를 회전함으로써 구현하였으며, 회전은 `android.view.animation` 패키지에 포함된 클래스를 사용해 만들었습니다. 특히 `android.view.animation.AnticipateOvershootInterpolator` 객체의 필드 값을 알맞게 조절하여 실제로 병이 돌아가는 모습과 유사하게 구현하였습니다.

세 가지 종류의 병을 선택할 수 있으며, 현실감을 더하기 위해 배경에 액체가 튀는 애니메이션을 추가하기도 하였습니다.

### 병 돌리기 게임 - AR 버전

AR 버전의 병 돌리기 게임은 카메라를 통해 평면이 인식되는 경우, 해당 평면에 선택된 모델을 올린 뒤 2D 버전과 동일하게 모델을 회전시켜 임의의 각도에서 멈추게 합니다. 주된 기능은 `com.google.ar:core`과 `com.google.ar.sceneform:core` 패키지를 이용해 구현하였습니다. 여러 가지 병을 비롯해 귀여운 고양이와 무서운 칼을 올려 회전시킬 수도 있습니다.

# Attribution

다음은 해당 프로젝트에 사용된 3D 모델들입니다

- [Bottle of Champagne](https://poly.google.com/view/fCWg2Z6OSku) by [Poly by Google](https://poly.google.com/user/4aEd8rQgKu2) is licensed under [CC-BY](https://creativecommons.org/licenses/by/3.0/legalcode)
- [Soju](https://poly.google.com/view/0FJq5yTfjg5) by [Jimi Youm](https://poly.google.com/user/fPd6c2uDY_w) is licensed under [CC-BY](https://creativecommons.org/licenses/by/3.0/legalcode)
- [Knife](https://poly.google.com/view/0X5xcxjszwI) by [Poly by Google](https://poly.google.com/user/4aEd8rQgKu2) is licensed under [CC-BY](https://creativecommons.org/licenses/by/3.0/legalcode)
- [Cat](https://poly.google.com/view/6dM1J6f6pm9) by [Poly by Google](https://poly.google.com/user/4aEd8rQgKu2) is licensed under [CC-BY](https://creativecommons.org/licenses/by/3.0/legalcode)
- [Soda](https://poly.google.com/view/2w4Av7jJ2_B) by [Poly by Google](https://poly.google.com/user/4aEd8rQgKu2) is licensed under [CC-BY](https://creativecommons.org/licenses/by/3.0/legalcode)

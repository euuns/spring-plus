# SPRING PLUS

플러스 주차 개인 과제

기간: 2025-03-10 ~ 2025-03-20


<br>

### 🛠 목차

1. [⛑️ 대용량 트래픽 처리](#-대용량-트래픽-성능-개선)
2. [📥 AWS 연결](#-aws-활용)


<br><br><br>



## ⛑ 대용량 트래픽 성능 개선

### 구현 과정

- [JDBC batchUpdate로 대용량 데이터 저장](https://rvrlo.tistory.com/entry/SpringBootJPA-JDBC-Batch-Insert%EB%A5%BC-%ED%86%B5%ED%95%B4-%EB%8C%80%EC%9A%A9%EB%9F%89-%EB%8D%B0%EC%9D%B4%ED%84%B0-%EC%A0%80%EC%9E%A5%ED%95%98%EA%B8%B0)

- [FULLTEXT index를 사용한 조회 성능 개선](https://rvrlo.tistory.com/entry/SpringBootJPA-%EC%A1%B0%ED%9A%8C-%EC%84%B1%EB%8A%A5-%EA%B0%9C%EC%84%A0%ED%95%98%EA%B8%B0-FULLTEXT-Index-%EC%82%AC%EC%9A%A9)



### 결과 비교

![{E67E096D-7E3A-46AE-B230-A83B24AC3E83}](https://github.com/user-attachments/assets/03b2f99d-60bf-4c7f-9586-1cf1d453e69e)


<br><br><br/>


## 📥 AWS 활용
- 연결만 구성

<br/><br/>

### IAM


#### admin 권한

![iam-1-user](https://github.com/user-attachments/assets/f2e7095d-6e4c-4593-a269-cf28341b3481)

<br/>

#### code deploy + s3 권한

![iam-2-user](https://github.com/user-attachments/assets/d3a82095-cebc-4705-bc8d-ba45e4cff2fd)

<br/><br/>
---
<br/><br/>

### EC2


![ec2-1-main](https://github.com/user-attachments/assets/f933ba09-0fa9-4dbb-85fe-0af16ebd619c)

<br/><br/>

![ec2-2-security-inbound](https://github.com/user-attachments/assets/bbf3be2b-5abb-4f8f-a3fa-97b20b0530c0)

<br/><br/>
---
<br/><br/>

### RDS


![rds-1-main](https://github.com/user-attachments/assets/75066bca-23b1-439e-9309-748686ccd1e6)

<br/><br/>

![rds-2-security-inbound](https://github.com/user-attachments/assets/c6898efe-afb9-4486-9cb0-b7db522f7771)

<br/><br/>
---
<br/><br/>

### S3


![s3-1-main](https://github.com/user-attachments/assets/db1b88a9-cedf-42e1-bf69-f921476c4391)

<br/><br/>

#### Github main에 push 후, 자동 배포

![s3-2-directory-projectzip](https://github.com/user-attachments/assets/c7c3ac11-4be1-4c53-a09d-baebd4abfe2e)


<br/><br/>
---
<br/><br/>

### EC2에 올라간 애플리케이션
![{A44856AB-6AE5-423F-B194-3796A2617801}](https://github.com/user-attachments/assets/8c511f7f-b4bb-4bcd-a773-893e373c4f4d)

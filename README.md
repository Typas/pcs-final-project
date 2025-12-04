# pcs-final-project
Simple idea on verifying the identity with PCS.

## Architecture
```mermaid
flowchart TB
    telecom["電信"]
    company["店到店"]
    phone["手機"]
    company-->|"0.送電話號碼、代號"| telecom
    telecom-->|"1.送QR code"| company
    telecom-->|"3.送驗證結果"| company
    telecom-->|"1.通知到貨(選擇性)"| phone
    company-->|"2-1.掃描QR code"| phone
    phone-->|"2-2.送驗證簡訊"| telecom
```

## State Machine of SMS
```mermaid
stateDiagram-v2
[*] --> Pending
Pending --> Pending
Pending --> Accept
Pending --> Reject
Accept --> [*]
Reject --> [*]
```

## The Flow
```mermaid
sequenceDiagram
participant telecom as "電信"
participant company as "店到店"
participant phone as "手機"
company->>telecom: "電話號碼、代號"
telecom->>company: "unique hash"
telecom->>phone: "到貨通知(選擇性)"
```

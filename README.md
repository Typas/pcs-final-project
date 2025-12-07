# pcs-final-project
Simple idea on verifying the identity with PCS.

## Architecture
```mermaid
flowchart TB
    Phone["手機"]
    
    subgraph TelecomCloud["電信"]
        TelecomGW[Gateway]
        TelecomDB["資料庫"]
        SMS["簡訊服務"]
    end
    
    subgraph EnterpriseCloud["店到店"]
        EnterpriseGW[Gateway]
        EnterpriseDB["資料庫"]
        Terminal["店內機器"]
    end
    
    EnterpriseGW -->|"手機號碼、\n驗證請求、\n(訊息)"| TelecomGW
    TelecomGW -->|"Hash值/拒絕請求"| EnterpriseGW
    TelecomGW --> TelecomDB
    TelecomDB --> TelecomGW
    TelecomDB --> SMS
    SMS --> TelecomDB
    
    TelecomGW -->|"確認/(超時)"| EnterpriseGW
    
    EnterpriseGW --> EnterpriseDB
    EnterpriseDB --> EnterpriseGW
    EnterpriseDB --> |"Hash值"| Terminal
    EnterpriseDB --> |"驗證成功"| Terminal

    Terminal --> |"Hash值 (QR)"| Phone
    
    SMS -->|"通知/警示"| Phone
    Phone -->|"確認 (Hash)"| SMS
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
participant telecom as 電信
participant company as 店到店
participant phone as 手機
company->>telecom: 1. 電話號碼、代號
telecom->>company: 1-1. 獨特hash值
telecom->>phone: 1-2. 到貨通知(選擇性)
company->>phone: 2-1. QR code(獨特hash值)
phone->>telecom: 2-2. 驗證簡訊
telecom->>company: 3. 驗證結果
```

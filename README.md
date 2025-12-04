# pcs-final-project
Simple idea on verifying the identity with PCS.

```mermaid
flowchart LR;
店到店-->|0.送電話號碼、貨物代號| 電信;
電信-->|1.送QR code| 店到店;
電信-->|3.送驗證結果| 店到店;
電信-->手機: 1.通知到貨(選擇性);
店到店-->手機 : 2-1.掃描QR code;
手機-->電信 : 2-2.送驗證簡訊;
```

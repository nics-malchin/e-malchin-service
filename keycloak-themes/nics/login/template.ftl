<#macro registrationLayout bodyClass="" displayInfo=false displayMessage=true displayRequiredFields=false pageTitle="">
<!DOCTYPE html>
<html<#if realm.internationalizationEnabled> lang="${locale.currentLanguageTag}"</#if>>
<head>
    <meta charset="utf-8">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="robots" content="noindex, nofollow">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>NICS — Нэвтрэх</title>
    <link rel="icon" href="${url.resourcesPath}/img/nics-logo.png"/>
    <style>
html,body,div,span,form,input,button,a,p,label,ul,li{margin:0;padding:0;border:0}
*,*::before,*::after{box-sizing:border-box}
body{min-height:100vh;font-family:Inter,"Segoe UI",-apple-system,Arial,sans-serif;background:#0C4D3F}
.nics-login-layout{position:relative;min-height:100vh;display:flex;align-items:center;
justify-content:flex-end;padding:3rem 8rem 3rem 3rem;overflow:hidden}
.nics-login-layout::before{content:"";position:absolute;inset:0;
background:url("${url.resourcesPath}/img/background.jpg") center center/cover no-repeat;
transform:scale(1.01)}
.nics-login-layout::after{content:"";position:absolute;inset:0;
background:linear-gradient(90deg,rgba(15,30,20,.15) 0%,rgba(10,20,15,.05) 100%)}
.nics-login-card-wrap{position:relative;z-index:1;width:100%;max-width:420px}
.nics-login-card{width:100%;padding:2.5rem 2.25rem 2rem;border-radius:20px;background:#fff;
box-shadow:0 30px 70px rgba(0,0,0,.4),0 0 0 1px rgba(255,255,255,.06)}
.nics-login-brand{text-align:center;margin-bottom:1.75rem}
.nics-login-brand__logo{height:90px;width:auto;max-width:100%;object-fit:contain}
.nics-login-page-title{margin:-0.5rem 0 1.25rem;color:#374151;font-size:.9375rem;font-weight:600;text-align:center}
.nics-login-form{display:flex;flex-direction:column;gap:1rem}
.nics-field-group{display:flex;flex-direction:column}
.nics-field-label{margin:0 0 .4rem .2rem;color:#374151;font-size:.875rem;font-weight:600}
.nics-field-input{width:100%;padding:.75rem 1rem;border:1.5px solid #d1d5db;border-radius:10px;
background:#f9fafb;color:#111827;font-size:.9375rem;outline:none;
transition:border-color .18s,box-shadow .18s,background .18s;font-family:inherit}
.nics-field-input:focus{border-color:#0C4D3F;background:#fff;box-shadow:0 0 0 3px rgba(12,77,63,.12)}
.nics-field-error{margin-top:.35rem;margin-left:.2rem;color:#dc2626;font-size:.8125rem;font-weight:500}
.nics-field-password-wrap{position:relative;display:flex;align-items:center}
.nics-field-password-wrap .nics-field-input{padding-right:2.75rem}
.nics-password-toggle{position:absolute;right:.75rem;background:none;border:none;cursor:pointer;
color:#9ca3af;padding:.2rem;display:flex;align-items:center;transition:color .15s}
.nics-password-toggle svg{width:1.125rem;height:1.125rem}
.nics-password-toggle:hover,.nics-password-toggle--active{color:#0C4D3F}
.nics-login-meta{display:flex;align-items:center;justify-content:space-between;gap:.75rem;margin-top:-.25rem}
.nics-login-checkbox{display:inline-flex;align-items:center;gap:.4rem;color:#6b7280;font-size:.8125rem;cursor:pointer}
.nics-login-link{color:#0C4D3F;text-decoration:none;font-size:.8125rem;font-weight:600}
.nics-login-link:hover{text-decoration:underline}
.nics-login-button{width:100%;margin-top:.25rem;padding:.8125rem 1.25rem;border:none;border-radius:10px;
background:#0C4D3F;color:#fff;font-size:.9375rem;font-weight:700;cursor:pointer;
letter-spacing:.01em;transition:background .18s,transform .1s;font-family:inherit;display:block;text-align:center;text-decoration:none}
.nics-login-button:hover{background:#0a3d32}
.nics-login-button:active{transform:scale(.985)}
.nics-login-button--outline{background:transparent;border:2px solid #0C4D3F;color:#0C4D3F}
.nics-login-button--outline:hover{background:rgba(12,77,63,.06)}
.nics-login-alert{margin-bottom:.875rem;padding:.7rem .9rem;border-radius:10px;
font-size:.875rem;font-weight:500;background:#fee2e2;color:#991b1b;border:1px solid #fca5a5}
.nics-login-alert--success{background:#d1fae5;color:#065f46;border-color:#6ee7b7}
.nics-login-alert--info{background:#dbeafe;color:#1e40af;border-color:#93c5fd}
.nics-login-alert--warning{background:#fef3c7;color:#92400e;border-color:#fcd34d}
.nics-login-info{margin-top:1.25rem;padding-top:1.25rem;border-top:1px solid #f3f4f6;
text-align:center;color:#6b7280;font-size:.875rem}
.nics-login-icon-box{display:flex;justify-content:center;margin-bottom:1.25rem}
.nics-login-icon-box svg{width:3rem;height:3rem;stroke:#0C4D3F;fill:none}
.nics-login-icon-box--success svg{stroke:#059669}
.nics-login-icon-box--error svg{stroke:#dc2626}
.nics-login-icon-box--warning svg{stroke:#d97706}
.nics-login-info-text{color:#4b5563;font-size:.9rem;text-align:center;line-height:1.65;margin-bottom:1.25rem}
.nics-login-info-text--error{color:#dc2626}
.nics-login-back{display:block;text-align:center;margin-top:1.1rem;color:#0C4D3F;
text-decoration:none;font-weight:600;font-size:.875rem}
.nics-login-back:hover{text-decoration:underline}
.nics-login-required-actions{margin:0 0 1rem 1.25rem;padding:0;color:#4b5563;font-size:.875rem;line-height:1.8}
@media(max-width:1200px){.nics-login-layout{padding-right:4rem}}
@media(max-width:768px){
  .nics-login-layout{justify-content:center;padding:1.5rem}
  .nics-login-card{padding:2rem 1.5rem 1.75rem;border-radius:16px}
  .nics-login-brand__logo{height:70px}
}
    </style>
</head>
<body>
    <div class="nics-login-layout">
        <div class="nics-login-card-wrap">
            <div class="nics-login-card">

                <div class="nics-login-brand">
                    <img src="${url.resourcesPath}/img/nics-logo.png" alt="NICS" class="nics-login-brand__logo"/>
                </div>

                <#if pageTitle?has_content>
                    <div class="nics-login-page-title">${pageTitle}</div>
                </#if>

                <#if displayMessage && message?has_content>
                    <div class="nics-login-alert nics-login-alert--${message.type}">
                        ${kcSanitize(message.summary)?no_esc}
                    </div>
                </#if>

                <#nested "form">

                <#if displayInfo>
                    <div class="nics-login-info">
                        <#nested "info">
                    </div>
                </#if>

            </div>
        </div>
    </div>
</body>
</html>
</#macro>

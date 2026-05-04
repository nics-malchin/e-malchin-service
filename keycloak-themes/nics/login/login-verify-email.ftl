<#import "template.ftl" as layout>
<@layout.registrationLayout
    displayMessage=false
    pageTitle="Имэйл баталгаажуулах"; section>

    <#if section = "form">
        <div class="nics-login-icon-box">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
                <polyline points="22,6 12,13 2,6"/>
            </svg>
        </div>

        <p class="nics-login-info-text">
            <strong>${user.email!''}</strong> хаяг руу баталгаажуулах имэйл илгээлээ.<br/>
            Имэйлийн холбоос дээр дарж нэвтрэлтээ баталгаажуулна уу.
        </p>

        <a class="nics-login-button nics-login-button--outline"
           href="${url.loginAction}"
           style="display:block;text-align:center;text-decoration:none;margin-top:0.5rem;">
            Дахин илгээх
        </a>

        <a class="nics-login-back" href="${url.loginUrl}">
            &#8592; Нэвтрэх хуудас руу буцах
        </a>
    </#if>
</@layout.registrationLayout>

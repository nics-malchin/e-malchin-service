<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=false pageTitle="Алдаа гарлаа"; section>
    <#if section = "form">
        <div class="nics-login-icon-box nics-login-icon-box--error">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="10"/>
                <line x1="12" y1="8" x2="12" y2="12"/>
                <line x1="12" y1="16" x2="12.01" y2="16"/>
            </svg>
        </div>

        <p class="nics-login-info-text nics-login-info-text--error">
            ${kcSanitize(message.summary)?no_esc}
        </p>

        <#if client?? && client.baseUrl?has_content>
            <a class="nics-login-button" href="${client.baseUrl}"
               style="display:block;text-align:center;text-decoration:none;margin-top:1rem;">
                Програм руу буцах
            </a>
        </#if>

        <a class="nics-login-back" href="${properties.kcLoginPage!''}">
            &#8592; Нэвтрэх хуудас руу буцах
        </a>
    </#if>
</@layout.registrationLayout>

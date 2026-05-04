<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=false; section>
    <#if section = "form">
        <div class="nics-login-icon-box nics-login-icon-box--success">
            <svg viewBox="0 0 24 24" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
                <polyline points="22 4 12 14.01 9 11.01"/>
            </svg>
        </div>

        <#if messageHeader??>
            <div class="nics-login-page-title">${kcSanitize(messageHeader)?no_esc}</div>
        </#if>

        <p class="nics-login-info-text">
            ${kcSanitize(message.summary)?no_esc}
        </p>

        <#if requiredActions??>
            <ul class="nics-login-required-actions">
                <#list requiredActions as reqAction>
                    <li><#switch reqAction>
                        <#case "CONFIGURE_TOTP">Нэг удаагийн нууц үг тохируулах<#break>
                        <#case "UPDATE_PASSWORD">Нууц үг шинэчлэх<#break>
                        <#case "UPDATE_PROFILE">Мэдээлэл шинэчлэх<#break>
                        <#case "VERIFY_EMAIL">Имэйл баталгаажуулах<#break>
                        <#default>${reqAction}
                    </#switch></li>
                </#list>
            </ul>
        </#if>

        <#if actionUri?has_content>
            <a class="nics-login-button" href="${actionUri}">Үргэлжлүүлэх</a>
        </#if>

        <#if client?? && client.baseUrl?has_content>
            <a class="nics-login-back" href="${client.baseUrl}">&#8592; Програм руу буцах</a>
        <#else>
            <a class="nics-login-back" href="${url.loginUrl}">&#8592; Нэвтрэх хуудас руу буцах</a>
        </#if>
    </#if>
</@layout.registrationLayout>

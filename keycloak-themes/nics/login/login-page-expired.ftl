<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=false pageTitle="Хуудасны хугацаа дууссан"; section>
    <#if section = "form">
        <div class="nics-login-icon-box nics-login-icon-box--warning">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="10"/>
                <polyline points="12 6 12 12 16 14"/>
            </svg>
        </div>

        <p class="nics-login-info-text">
            Нэвтрэх хуудасны хугацаа дууссан байна. Дахин оролдоно уу.
        </p>

        <div class="nics-login-form" style="gap:0.75rem;display:flex;flex-direction:column;">
            <a class="nics-login-button" href="${url.loginRestartFlowUrl}"
               style="display:block;text-align:center;text-decoration:none;">
                Дахин эхлэх
            </a>
            <#if client?? && client.baseUrl?has_content>
                <a class="nics-login-button nics-login-button--outline" href="${client.baseUrl}"
                   style="display:block;text-align:center;text-decoration:none;">
                    Програм руу буцах
                </a>
            </#if>
        </div>
    </#if>
</@layout.registrationLayout>

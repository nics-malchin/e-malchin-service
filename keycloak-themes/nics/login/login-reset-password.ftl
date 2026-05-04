<#import "template.ftl" as layout>
<@layout.registrationLayout
    displayInfo=true
    displayMessage=!messagesPerField.existsError('username')
    pageTitle="Нууц үг сэргээх"; section>

    <#if section = "form">
        <form id="kc-reset-password-form" class="nics-login-form" action="${url.loginAction}" method="post">
            <div class="nics-field-group">
                <label for="username" class="nics-field-label">
                    <#if !realm.loginWithEmailAllowed>Хэрэглэгчийн нэр<#elseif !realm.registrationEmailAsUsername>Хэрэглэгчийн нэр эсвэл имэйл<#else>Имэйл хаяг</#if>
                </label>
                <input tabindex="1" id="username" class="nics-field-input" name="username"
                       autofocus value="${(auth.attemptedUsername!'')}" type="text"
                       placeholder="Имэйл хаяг" autocomplete="email"
                       aria-invalid="<#if messagesPerField.existsError('username')>true</#if>"/>
                <#if messagesPerField.existsError('username')>
                    <div class="nics-field-error">${kcSanitize(messagesPerField.get('username'))?no_esc}</div>
                </#if>
            </div>

            <button class="nics-login-button" type="submit" tabindex="2">
                Сэргээх холбоос илгээх
            </button>

            <a class="nics-login-back" href="${url.loginUrl}" tabindex="3">
                &#8592; Нэвтрэх хуудас руу буцах
            </a>
        </form>

    <#elseif section = "info">
        <div class="nics-login-info-text">
            Таны бүртгэлтэй имэйл хаяг руу нууц үг сэргээх холбоос илгээгдэх болно.
        </div>
    </#if>
</@layout.registrationLayout>

<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('username','password') displayInfo=realm.password && realm.registrationAllowed && !registrationDisabled??; section>
    <#if section = "form">
        <form id="kc-form-login" class="nics-login-form" onsubmit="login.disabled = true; return true;" action="${url.loginAction}" method="post">
            <div class="nics-field-group">
                <label for="username" class="nics-field-label">
                    <#if !realm.loginWithEmailAllowed>${msg("username")}<#elseif !realm.registrationEmailAsUsername>${msg("usernameOrEmail")}<#else>${msg("email")}</#if>
                </label>
                <input tabindex="1"
                       id="username"
                       class="nics-field-input"
                       name="username"
                       value="${(login.username!'')}"
                       type="text"
                       placeholder="Email address"
                       autofocus
                       autocomplete="username"
                       aria-invalid="<#if messagesPerField.existsError('username','password')>true</#if>" />
            </div>

            <div class="nics-field-group">
                <label for="password" class="nics-field-label">${msg("password")}</label>
                <input tabindex="2"
                       id="password"
                       class="nics-field-input"
                       name="password"
                       type="password"
                       placeholder="Password"
                       autocomplete="current-password"
                       aria-invalid="<#if messagesPerField.existsError('username','password')>true</#if>" />
                <#if messagesPerField.existsError('username','password')>
                    <div class="nics-field-error">${kcSanitize(messagesPerField.getFirstError('username','password'))?no_esc}</div>
                </#if>
            </div>

            <button tabindex="4" class="nics-login-button" name="login" id="kc-login" type="submit">
                ${msg("doLogIn")}
            </button>

            <div class="nics-login-meta">
                <#if realm.rememberMe && !usernameEditDisabled??>
                    <label class="nics-login-checkbox">
                        <input tabindex="3" id="rememberMe" name="rememberMe" type="checkbox" <#if login.rememberMe??>checked</#if>>
                        <span>Сануулах</span>
                    </label>
                </#if>
                <#if realm.resetPasswordAllowed>
                    <a class="nics-login-link" tabindex="5" href="${url.loginResetCredentialsUrl}">Нууц үг мартсан?</a>
                </#if>
            </div>
        </form>
    <#elseif section = "info">
        <#if realm.password && realm.registrationAllowed && !registrationDisabled??>
            <div class="nics-login-info">
                <span>${msg("noAccount")}</span>
                <a class="nics-login-link" tabindex="6" href="${url.registrationUrl}">${msg("doRegister")}</a>
            </div>
        </#if>
    </#if>
</@layout.registrationLayout>

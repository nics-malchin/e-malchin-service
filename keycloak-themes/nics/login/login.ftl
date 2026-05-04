<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('username','password') displayInfo=realm.password && realm.registrationAllowed && !registrationDisabled??; section>
    <#if section = "form">
        <form id="kc-form-login" class="nics-login-form" onsubmit="login.disabled = true; return true;" action="${url.loginAction}" method="post">
            <div class="nics-field-group">
                <label for="username" class="nics-field-label">
                    <#if !realm.loginWithEmailAllowed>Хэрэглэгчийн нэр<#elseif !realm.registrationEmailAsUsername>Хэрэглэгчийн нэр эсвэл имэйл<#else>Имэйл хаяг</#if>
                </label>
                <input tabindex="1" id="username" class="nics-field-input" name="username"
                       value="${(login.username!'')}" type="text"
                       placeholder="Нэвтрэх нэр эсвэл имэйл"
                       autofocus autocomplete="username"
                       aria-invalid="<#if messagesPerField.existsError('username','password')>true</#if>"/>
            </div>

            <div class="nics-field-group">
                <label for="password" class="nics-field-label">Нууц үг</label>
                <div class="nics-field-password-wrap">
                    <input tabindex="2" id="password" class="nics-field-input" name="password"
                           type="password" placeholder="Нууц үг"
                           autocomplete="current-password"
                           aria-invalid="<#if messagesPerField.existsError('username','password')>true</#if>"/>
                    <button type="button" class="nics-password-toggle"
                            onclick="nicsTogglePwd('password',this)" aria-label="Нууц үг харуулах">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/>
                        </svg>
                    </button>
                </div>
                <#if messagesPerField.existsError('username','password')>
                    <div class="nics-field-error">${kcSanitize(messagesPerField.getFirstError('username','password'))?no_esc}</div>
                </#if>
            </div>

            <div class="nics-login-meta">
                <#if realm.rememberMe && !usernameEditDisabled??>
                    <label class="nics-login-checkbox">
                        <input tabindex="3" id="rememberMe" name="rememberMe" type="checkbox"
                               <#if login.rememberMe??>checked</#if>>
                        <span>Сануулах</span>
                    </label>
                </#if>
                <#if realm.resetPasswordAllowed>
                    <a class="nics-login-link" tabindex="5" href="${url.loginResetCredentialsUrl}">Нууц үг мартсан?</a>
                </#if>
            </div>

            <button tabindex="4" class="nics-login-button" name="login" id="kc-login" type="submit">
                Нэвтрэх
            </button>
        </form>
        <script>
        function nicsTogglePwd(id, btn) {
            var f = document.getElementById(id);
            var show = f.type === 'password';
            f.type = show ? 'text' : 'password';
            btn.classList.toggle('nics-password-toggle--active', show);
        }
        </script>
    <#elseif section = "info">
        <#if realm.password && realm.registrationAllowed && !registrationDisabled??>
            <div class="nics-login-info">
                <span>Бүртгэлгүй юу? </span>
                <a class="nics-login-link" tabindex="6" href="${url.registrationUrl}">Бүртгүүлэх</a>
            </div>
        </#if>
    </#if>
</@layout.registrationLayout>

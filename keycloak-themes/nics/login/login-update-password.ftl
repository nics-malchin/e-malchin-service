<#import "template.ftl" as layout>
<@layout.registrationLayout
    displayMessage=!messagesPerField.existsError('password','password-confirm')
    pageTitle="Шинэ нууц үг тохируулах"; section>

    <#if section = "form">
        <form id="kc-passwd-update-form" class="nics-login-form" action="${url.loginAction}" method="post">
            <input type="text" id="username" name="username"
                   value="${username}" readonly="readonly"
                   autocomplete="username" style="display:none;" />
            <input type="hidden" id="id-hidden-input" name="credentialId"
                   value="${(currentCredentialId!'')}" />

            <div class="nics-field-group">
                <label for="password-new" class="nics-field-label">Шинэ нууц үг</label>
                <div class="nics-field-password-wrap">
                    <input tabindex="1"
                           id="password-new"
                           name="password-new"
                           class="nics-field-input"
                           type="password"
                           placeholder="Шинэ нууц үг"
                           autofocus
                           autocomplete="new-password"
                           aria-invalid="<#if messagesPerField.existsError('password')>true</#if>" />
                    <button type="button" class="nics-password-toggle"
                            onclick="nicsTogglePwd('password-new',this)"
                            aria-label="Нууц үг харуулах">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                            <circle cx="12" cy="12" r="3"/>
                        </svg>
                    </button>
                </div>
                <#if messagesPerField.existsError('password')>
                    <div class="nics-field-error">${kcSanitize(messagesPerField.get('password'))?no_esc}</div>
                </#if>
            </div>

            <div class="nics-field-group">
                <label for="password-confirm" class="nics-field-label">Нууц үг давтах</label>
                <div class="nics-field-password-wrap">
                    <input tabindex="2"
                           id="password-confirm"
                           name="password-confirm"
                           class="nics-field-input"
                           type="password"
                           placeholder="Нууц үгийг дахин оруулна уу"
                           autocomplete="new-password"
                           aria-invalid="<#if messagesPerField.existsError('password-confirm')>true</#if>" />
                    <button type="button" class="nics-password-toggle"
                            onclick="nicsTogglePwd('password-confirm',this)"
                            aria-label="Нууц үг харуулах">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                            <circle cx="12" cy="12" r="3"/>
                        </svg>
                    </button>
                </div>
                <#if messagesPerField.existsError('password-confirm')>
                    <div class="nics-field-error">${kcSanitize(messagesPerField.get('password-confirm'))?no_esc}</div>
                </#if>
            </div>

            <button tabindex="3" class="nics-login-button" type="submit">
                Нууц үг хадгалах
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
    </#if>
</@layout.registrationLayout>

<#macro registrationLayout bodyClass="" displayInfo=false displayMessage=true displayRequiredFields=false>
<!DOCTYPE html>
<html class="${properties.kcHtmlClass!}"<#if realm.internationalizationEnabled> lang="${locale.currentLanguageTag}"</#if>>
<head>
    <meta charset="utf-8">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="robots" content="noindex, nofollow">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>${msg("loginTitle",(realm.displayName!'NICS'))}</title>
    <link rel="icon" href="${url.resourcesPath}/img/logo_green.png" />
    <#if properties.stylesCommon?has_content>
        <#list properties.stylesCommon?split(' ') as style>
            <link href="${url.resourcesCommonPath}/${style}" rel="stylesheet" />
        </#list>
    </#if>
    <#if properties.styles?has_content>
        <#list properties.styles?split(' ') as style>
            <link href="${url.resourcesPath}/${style}" rel="stylesheet" />
        </#list>
    </#if>
</head>
<body class="${properties.kcBodyClass!} nics-login-body ${bodyClass}">
    <div class="nics-login-layout">
        <div class="nics-login-card-wrap">
            <div class="nics-login-card">
                <div class="nics-login-brand">
                    <img src="${url.resourcesPath}/img/asset-3.png" alt="NICS logo" class="nics-login-brand__logo" />
                    <div class="nics-login-brand__title">E-Malchin NICS</div>
                </div>

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

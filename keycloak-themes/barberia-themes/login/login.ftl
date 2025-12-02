<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Barbería - Iniciar sesión</title>
    <link rel="stylesheet" type="text/css" href="${url.resourcesPath}/css/styles.css">
</head>
<body>
<div class="barberia-login-card">
    <h2>Iniciar sesión</h2>

    <#if message?has_content>
        <div class="barberia-error">
            ${message.summary}
        </div>
    </#if>

    <form id="kc-form-login" action="${url.loginAction}" method="post">
        <input
                type="text"
                id="username"
                name="username"
                placeholder="Usuario"
                value="${(login.username!'')}"
                autofocus
                required />

        <input
                type="password"
                id="password"
                name="password"
                placeholder="Contraseña"
                required />

        <button type="submit">
            Iniciar sesión
        </button>

        <div class="barberia-register-link">
            ¿No tienes cuenta?
            <a href="${url.registrationUrl}">Regístrate aquí</a>
        </div>
    </form>
</div>
</body>
</html>
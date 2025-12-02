<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Barbería - Iniciar sesión</title>
    <link rel="stylesheet" type="text/css" href="${url.resourcesPath}/css/styles.css">
</head>
<body>
<div class="login-page">
    <div class="login-card">
        <div class="login-left">
            <!-- AQUÍ VA TU LOGO -->
            <img src="${url.resourcesPath}/logo.png" alt="Logo Barbería" class="login-logo">
        </div>

        <div class="login-right">
            <h2>Inicia sesión en tu cuenta</h2>
            <p class="subtitle">La mejor barberia de la cordillera.</p>

            <!-- Simulación de error -->
            <!-- <div class="barberia-error">
                Invalid username or password.
            </div> -->

            <form onsubmit="event.preventDefault(); alert('Login submitted!')">
                <label for="username">Nombre de usuario:</label>
                <input
                        type="text"
                        id="username"
                        name="username"
                        autofocus
                        required
                        placeholder="Ingresa tu usuario" />

                <label for="password">Contraseña</label>
                <input
                        type="password"
                        id="password"
                        name="password"
                        required
                        placeholder="Ingresa tu contraseña" />

                <div class="form-extra">
                    <label class="remember-me">
                        <input type="checkbox" name="rememberMe" />
                        Recuerdame
                    </label>
                    <a class="forgot-password" href="http://localhost:4200/actualizardatos">Contraseña olvidada.</a>
                </div>

                <button type="submit">
                    Ingresar
                </button>
            </form>

            <div class="barberia-register-link">

            <a href="http://localhost:4200/register">Regístrate aquí</a>
            </div>
        </div>
    </div>

</div>
</body>
</html>


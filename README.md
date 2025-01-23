# GranAccess

**GranAccess** es una aplicación desarrollada como parte de la asignatura **Dirección y Gestión de Proyectos** en la **Universidad de Granada** durante el curso académico 2024/2025. El objetivo principal de la aplicación es proporcionar un gestor de tareas accesible para un colegio pequeño, con especial atención a las necesidades de accesibilidad de los alumnos.

## Tabla de Contenidos

- [Descripción General](#descripción-general)
  
- [Funcionalidades](#funcionalidades)
  
  - [Administrador](#administrador)
    
  - [Profesor](#profesor)
    
  - [Alumno](#alumno)
    
- [Tecnología](#tecnología)
  
- [Requisitos para Configuración](#requisitos-para-configuración)
  
- [Instalación](#instalación)
  
- [Autor](#autor)

---

## Descripción General

**GranAccess** está diseñada para facilitar la gestión y realización de tareas dentro de un entorno educativo. La aplicación admite diferentes tipos de usuarios (alumnos, profesores y administrador), y se centra en ofrecer una interfaz accesible adaptada a las preferencias visuales de los alumnos, permitiendo el uso exclusivo de texto, imágenes y audio para interactuar con los contenidos.

## Funcionalidades

### Administrador
El administrador tiene un rol central y es el único usuario con acceso completo a todas las funciones de la aplicación. Sus responsabilidades incluyen:
- **Gestión de usuarios**: Crear y gestionar cuentas de alumnos y profesores.
- **Gestión de tareas**: Crear, modificar y asignar tareas a alumnos, además de establecer el orden de los pasos en las tareas.
- **Gestión del comedor**: Crear menús para el comedor escolar.

### Profesor
Los profesores pueden:
- **Solicitar materiales**: Enviar peticiones de materiales necesarios para sus clases.

### Alumno
Los alumnos tienen acceso a las siguientes funciones:
- **Realización de tareas**: Completar tareas asignadas, incluyendo tareas genéricas y tareas específicas como solicitudes de materiales.
- **Gestión del comedor**: Realizar comandas, especificando la cantidad de menús deseados por tipo.

## Tecnología

La aplicación está desarrollada en **Android Studio** y utiliza **Firebase** como base de datos en la nube. Se recomienda utilizar el plan **Blaze** (prepago) de Firebase para el correcto funcionamiento de las funcionalidades de backend.

## Requisitos para Configuración

Para probar esta aplicación, es necesario configurar un entorno de Firebase. Sigue los pasos a continuación:

1. Configura una base de datos activa en **Firebase** con el plan **Blaze**.
2. Integra la aplicación con tu base de datos de Firebase siguiendo la [documentación oficial de Firebase](https://firebase.google.com/docs).
3. Crea una colección `usuarios` en tu base de datos y añade un documento con un campo `rol` cuyo valor sea `admin`. Este será el usuario administrador predeterminado.

**Nota:** La base de datos Firebase utilizada para el desarrollo estará operativa únicamente hasta la finalización del primer cuatrimestre del curso 2024/2025. Posteriormente, los usuarios interesados deberán configurar su propia instancia de Firebase para ejecutar la aplicación.

## Instalación

1. Descarga o clona este repositorio:
   ```bash
   git clone https://github.com/tu-usuario/granaccess.git

2. Abre el proyecto en Android Studio.
   
3. Configura Firebase en el proyecto como se describe en la sección anterior.
   
4. Compila y ejecuta la aplicación en un dispositivo Android o emulador compatible.

## Autor
Alejandro Coman Venceslá.
Estudiante de Ingeniería Informática y Administración y Dirección de Empresas.
Universidad de Granada

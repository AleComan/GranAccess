## GranAccess

Aplicación desarrollada para la asignatura `Dirección y Gestión de Proyectos` de la `Universidad de Granada`. Curso 2024 / 2025

Simple gestor de tareas para un colegio pequeño, que cuenta con soporte para usuarios como Alumnos, Profesores y un administrador por defecto. Especializado en ofrecer una interfaz accesible para alumnos con preferencias visuales específicas, como mostrar contenidos solamente en formato de texto, imagen y audio.

Funciones del `administrador` (sólo hay un usuario administrador):
- Crear usuarios de alumno y profesores.
- Crear y modificar tareas, reasignando los alumnos a los que van dirigidas y posibilidad de modificar el orden de los pasos de las tareas.
- Crear menús del comedor.

Funciones del `profesor` (puede haber más de uno):
- Pedir materiales.

Funciones del `alumno`:
- Completar tareas genéricas.
- Completar tareas del tipo "Pedir Material".
- Pedir comandas del comedor (indicar cuántos menús de cada tipo se desean).

Esta aplicación está diseñada para funcionar con `Firebase`. En fechas futuras a la finalización del primer cuatrimestre del curso mencionado mi base de datos Firebase no estará operativa. 

Toda personas que desee probar la aplicación deberá tener activa una base de datos en Firebase con el plan `Blaze` (prepago), añadir la aplicación a la base de datos siguiendo el tutorial proporcionado por Firebase y crear una colección `usuarios` con un documento que tenga un campo `rol` con valor `admin`.

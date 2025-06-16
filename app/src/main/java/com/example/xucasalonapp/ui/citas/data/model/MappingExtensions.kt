package com.example.xucasalonapp.ui.citas.data.model


fun ServicioDTO.toDomain(): Servicio =
    Servicio(
        idServicio = this.idServicio,
        nombre = this.nombre,
        descripcion = this.descripcion,
        tipo = this.tipo,
        precio = this.precio,
        duracion = this.duracion
    )

fun CitaDTO.toDomain(): Cita =
    Cita(
        idCita = this.idCita,
        clienteId = this.idCliente,
        empleadoId = this.idEmpleado,
        fecha = this.fecha,
        horaInicio = this.horaInicio,
        horaFin = this.horaFin,
        estado = this.estado,
        notas = this.notas,
        fechaCreacion = this.fechaCreacion,
        citaServicios = this.servicios.map { svcDto ->
            CitaServicio(
                idCita = this.idCita,
                idServicio = svcDto.idServicio,
                servicio = svcDto.toDomain(),
                precioAplicado = svcDto.precio
            )
        }
    )
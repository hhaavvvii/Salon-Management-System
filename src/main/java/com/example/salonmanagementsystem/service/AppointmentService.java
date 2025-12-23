package com.example.salonmanagementsystem.service;

import com.example.salonmanagementsystem.app.SessionContext;
import com.example.salonmanagementsystem.dao.AppointmentDao;
import com.example.salonmanagementsystem.dao.impl.AppointmentDaoImpl;
import com.example.salonmanagementsystem.exceptions.ValidationException;
import com.example.salonmanagementsystem.model.Appointment;
import com.example.salonmanagementsystem.model.Role;
import com.example.salonmanagementsystem.model.User;

import java.util.List;

public class AppointmentService {

    private final AppointmentDao dao = new AppointmentDaoImpl();

    public List<Appointment> getAppointments() {
        User u = SessionContext.getCurrentUser();
        if (u.getRole() == Role.ADMIN) {
            return dao.findAll();
        }
        if (u.getEmployeeId() == null) {
            return List.of();
        }
        return dao.findByEmployee(u.getEmployeeId());
    }

    public void create(Appointment a) {

        User u = SessionContext.getCurrentUser();

        if (u.getRole() == Role.MASTER) {
            a.setEmployeeId(u.getEmployeeId());
        }

        if (dao.hasTimeConflict(
                a.getEmployeeId(),
                a.getStartTime(),
                a.getEndTime())) {

            throw new ValidationException(
                    "Time slot is already occupied"
            );
        }

        dao.insert(a);
    }


}

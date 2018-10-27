package ru.bmstu.schedule.csv.parser;

import ru.bmstu.schedule.csv.RecordHolder;
import ru.bmstu.schedule.csv.header.DepartmentHeader;
import ru.bmstu.schedule.entity.Department;

public class DepartmentParser implements Parser<Department> {
    @Override
    public Department parse(RecordHolder rec) {
        Department department = new Department();
        rec.fillInt(department::setNumber, DepartmentHeader.departmentNumber);
        rec.fillString(department::setTitle, DepartmentHeader.title);

        return department;
    }
}

package ru.bmstu.schedule.dbtools;

import ru.bmstu.schedule.entity.ClassTime;
import ru.bmstu.schedule.entity.Faculty;
import ru.bmstu.schedule.html.commons.Node;
import ru.bmstu.schedule.html.node.FacultyNode;
import ru.bmstu.schedule.html.node.ScheduleItemNode;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public abstract class EntityAdapter<E> {
    private Node node;

    EntityAdapter(Node node) {
        this.node = node;
    }

    public abstract E getEntity();

    Node getNode() {
        return node;
    }

    @SuppressWarnings("unchecked")
    public static<E> EntityAdapter<E> adapterFor(Class<E> clazz, Node node) throws NotImplementedException {
        if(clazz == Faculty.class) {
            return (EntityAdapter<E>) new EntityAdapter<Faculty>(node) {
                @Override
                public Faculty getEntity() {
                    Faculty fac = new Faculty();
                    FacultyNode node = (FacultyNode) this.getNode();
                    System.out.println("-> Faculty node: " + node.toString());
                    fac.setTitle(node.getTitle());
                    fac.setCipher(node.getCipher());

                   return fac;
                }
            };
        } else if(clazz == ClassTime.class) {
            return (EntityAdapter<E>) new EntityAdapter<ClassTime>(node) {
                @Override
                public ClassTime getEntity() {
                    ClassTime ctime = new ClassTime();
                    ScheduleItemNode itemNode = (ScheduleItemNode) this.getNode();
                    ctime.setStartsAt(itemNode.getStartsAt());
                    ctime.setEndsAt(itemNode.getEndsAt());

                    return ctime;
                }
            };
        }

        throw new NotImplementedException();
    }
}

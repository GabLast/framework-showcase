package com.showcase.application.services;

import com.showcase.application.utils.Utilities;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

public abstract class BaseService<T, ID> {

    private final Logger logger = Utilities.getLogger(BaseService.class);

    protected abstract JpaRepository<T, ID> getRepository();

    public Optional<T> get(ID id) {
        if (id == null) {
            return Optional.empty();
        }

        return this.getRepository().findById(id);
    }

    public List<T> getAll(List<ID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        return this.getRepository().findAllById(ids);
    }

    public T save(T entity) {
        return this.getRepository().save(entity);
    }

    public T saveAndFlush(T entity) {
        return this.getRepository().saveAndFlush(entity);
    }

    public List<T> saveAllAndFlush(Iterable<T> entities) {
        return this.getRepository().saveAllAndFlush(entities);
    }

    public Page<T> list(Pageable pageable) {
        return this.getRepository().findAll(pageable);
    }

    public List<T> findAll() {
        return this.getRepository().findAll();
    }

    public int count() {
        return (int) this.getRepository().count();
    }

    public void delete(T object) {
        this.getRepository().delete(object);
    }

    public void deleteById(ID id) {
        this.getRepository().deleteById(id);
    }

    public void disable(T object) {
        Field idField = Utilities.getIdField(object.getClass());
        if (idField == null) {
            logger.error("@ID of class " + object.getClass().getName() + " is not accessible");
            return;
        }

        idField.setAccessible(true);
        try {
            Object idValue = idField.get(object);
            disableById((ID) idValue);
        } catch (IllegalAccessException e) {
            logger.error("Class @ID field is not available: " + object.getClass().getName() + "\n" + e.getMessage());
        }
    }

    public void disableById(ID id) {
        Optional<T> optionalT = get(id);
        if (optionalT.isEmpty()) {
            logger.error("Selected object does not exist");
            return;
        }

        T entidad = optionalT.get();
        try {
            saveAndFlush(Utilities.setFieldValue(entidad, "enabled", false));
        } catch (NoSuchFieldException e) {
            logger.error("Class name " + entidad.getClass().getName() + " doesnt have the selected field");
        } catch (IllegalAccessException e) {
            logger.error("Class name " + entidad.getClass().getName() + " field access is not available");
        }
    }
}

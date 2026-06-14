package sube.interviews.mareoenvios.service;

import org.springframework.data.repository.CrudRepository;
import sube.interviews.mareoenvios.exception.ResourceNotFoundException;

/**
 * Template Method pattern.
 *
 * Define el flujo genérico para el CRUD.
 * Las subclases concretas inyectan su propio repositorio y pueden
 * sobreescribir los hooks validateBeforeSave / validateBeforeDelete
 * para agregar validaciones específicas sin repetir la lógica base.
 *
 * @param <T>  tipo de entidad
 * @param <ID> tipo de la clave primaria
 */
public abstract class BaseService<T, ID> {

    protected abstract CrudRepository<T, ID> getRepository();

    protected abstract String getEntityName();

    /** Hook: sobreescribir para validaciones previas al guardado. */
    protected void validateBeforeSave(T entity) { }

    /** Hook: sobreescribir para validaciones previas al borrado. */
    protected void validateBeforeDelete(ID id) { }

    protected T findById(ID id) {
        return getRepository().findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        getEntityName() + " not found with id: " + id
                ));
    }

    protected T save(T entity) {
        validateBeforeSave(entity);
        return getRepository().save(entity);
    }
}

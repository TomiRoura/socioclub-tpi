import PropTypes from 'prop-types';

// Muestra el resultado de una acción (inscribirse, darse de baja). Si el error vino de una
// regla de negocio (409), muestra también qué regla fue, por ejemplo "(RN1)".
function Mensaje({ tipo, texto, regla }) {
  return (
    <p className={`mensaje mensaje--${tipo}`} role="status">
      {texto}
      {regla && <span className="mensaje__regla"> ({regla})</span>}
    </p>
  );
}

Mensaje.propTypes = {
  tipo: PropTypes.oneOf(['exito', 'error']).isRequired,
  texto: PropTypes.string.isRequired,
  regla: PropTypes.string,
};

export default Mensaje;

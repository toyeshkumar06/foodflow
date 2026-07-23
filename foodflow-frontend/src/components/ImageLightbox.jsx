function ImageLightbox({ imageUrl, name, description, onClose }) {
  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="lightbox-content" onClick={(e) => e.stopPropagation()}>
        <button className="lightbox-close" onClick={onClose}>✕</button>
        <img src={imageUrl} alt={name} className="lightbox-image" />
        <div className="lightbox-caption">
          <h3>{name}</h3>
          {description && <p style={{ color: "var(--color-text-light)", marginTop: "6px" }}>{description}</p>}
        </div>
      </div>
    </div>
  );
}

export default ImageLightbox;
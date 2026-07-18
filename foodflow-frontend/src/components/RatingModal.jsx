import { useState } from "react";
import axiosInstance from "../api/axiosInstance";

function RatingModal({ orderId, targetLabel, ratingType, foodItemId, onClose, onSubmitted }) {
  const [stars, setStars] = useState(5);
  const [reviewText, setReviewText] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = async () => {
    setSubmitting(true);
    setError("");
    try {
      let url = `/orders/${orderId}/ratings/`;
      if (ratingType === "restaurant") url += "restaurant";
      else if (ratingType === "delivery-agent") url += "delivery-agent";
      else if (ratingType === "food") url += `food/${foodItemId}`;

      await axiosInstance.post(url, { stars, reviewText });
      onSubmitted();
      onClose();
    } catch (err) {
      setError(err.response?.data?.message || "Could not submit rating.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <h3 style={{ marginBottom: "4px" }}>Rate {targetLabel}</h3>
        <p style={{ color: "var(--color-text-light)", fontSize: "13px", marginBottom: "16px" }}>
          How was your experience?
        </p>

        <div className="star-picker">
          {[1, 2, 3, 4, 5].map((n) => (
            <span
              key={n}
              className={`star ${n <= stars ? "filled" : ""}`}
              onClick={() => setStars(n)}
            >
              ★
            </span>
          ))}
        </div>

        <textarea
          className="input-field"
          placeholder="Write a review (optional)"
          rows={3}
          style={{ marginTop: "16px", resize: "none" }}
          value={reviewText}
          onChange={(e) => setReviewText(e.target.value)}
        />

        {error && <p className="error-text" style={{ marginTop: "12px" }}>{error}</p>}

        <div style={{ display: "flex", gap: "10px", marginTop: "16px" }}>
          <button className="btn btn-secondary" onClick={onClose}>Cancel</button>
          <button className="btn btn-primary" onClick={handleSubmit} disabled={submitting}>
            {submitting ? "Submitting..." : "Submit Rating"}
          </button>
        </div>
      </div>
    </div>
  );
}

export default RatingModal;
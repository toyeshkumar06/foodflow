import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axiosInstance from "../api/axiosInstance";
import Navbar from "../components/Navbar";

function AdminCoupons() {
  const [coupons, setCoupons] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const [form, setForm] = useState({
    code: "", description: "", discountType: "PERCENTAGE", discountValue: "",
    minBillAmount: "0", maxDiscountAmount: "", expiryDate: "2026-12-31",
    usageLimit: "", firstOrderOnly: false,
  });

  const loadCoupons = () => {
    axiosInstance.get("/admin/coupons").then((res) => {
      setCoupons(res.data);
      setLoading(false);
    });
  };

  useEffect(() => { loadCoupons(); }, []);

  const handleCreate = async (e) => {
    e.preventDefault();
    setError("");
    try {
      const body = {
        ...form,
        discountValue: Number(form.discountValue),
        minBillAmount: form.minBillAmount ? Number(form.minBillAmount) : 0,
        maxDiscountAmount: form.maxDiscountAmount ? Number(form.maxDiscountAmount) : null,
        usageLimit: form.usageLimit ? Number(form.usageLimit) : null,
      };
      await axiosInstance.post("/admin/coupons", body);
      setShowForm(false);
      setForm({ code: "", description: "", discountType: "PERCENTAGE", discountValue: "", minBillAmount: "0", maxDiscountAmount: "", expiryDate: "2026-12-31", usageLimit: "", firstOrderOnly: false });
      loadCoupons();
    } catch (err) {
      setError(err.response?.data?.message || "Could not create coupon.");
    }
  };

  return (
    <>
      <Navbar />
      <div className="page-container" style={{ paddingTop: "40px", paddingBottom: "60px" }}>
        <button className="btn btn-secondary btn-small" onClick={() => navigate("/admin")} style={{ marginBottom: "20px" }}>
          ← Back to Dashboard
        </button>

        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "24px" }}>
          <h1 style={{ fontSize: "28px" }}>Coupons</h1>
          <button className="btn btn-primary btn-small" onClick={() => setShowForm(!showForm)}>
            {showForm ? "Cancel" : "+ New Coupon"}
          </button>
        </div>

        {showForm && (
          <form onSubmit={handleCreate} className="card" style={{ marginBottom: "24px", maxWidth: "480px" }}>
            <div className="form-group">
              <label>Code</label>
              <input className="input-field" value={form.code} onChange={(e) => setForm({ ...form, code: e.target.value.toUpperCase() })} required />
            </div>
            <div className="form-group">
              <label>Description</label>
              <input className="input-field" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
            </div>
            <div className="form-group">
              <label>Discount Type</label>
              <select className="input-field" value={form.discountType} onChange={(e) => setForm({ ...form, discountType: e.target.value })}>
                <option value="PERCENTAGE">Percentage</option>
                <option value="FLAT">Flat Amount</option>
              </select>
            </div>
            <div className="form-group">
              <label>Discount Value {form.discountType === "PERCENTAGE" ? "(%)" : "(₹)"}</label>
              <input type="number" className="input-field" value={form.discountValue} onChange={(e) => setForm({ ...form, discountValue: e.target.value })} required />
            </div>
            <div className="form-group">
              <label>Minimum Bill Amount (₹)</label>
              <input type="number" className="input-field" value={form.minBillAmount} onChange={(e) => setForm({ ...form, minBillAmount: e.target.value })} />
            </div>
            {form.discountType === "PERCENTAGE" && (
              <div className="form-group">
                <label>Max Discount Cap (₹, optional)</label>
                <input type="number" className="input-field" value={form.maxDiscountAmount} onChange={(e) => setForm({ ...form, maxDiscountAmount: e.target.value })} />
              </div>
            )}
            <div className="form-group">
              <label>Expiry Date</label>
              <input type="date" className="input-field" value={form.expiryDate} onChange={(e) => setForm({ ...form, expiryDate: e.target.value })} required />
            </div>
            <div className="form-group">
              <label>Usage Limit (optional, blank = unlimited)</label>
              <input type="number" className="input-field" value={form.usageLimit} onChange={(e) => setForm({ ...form, usageLimit: e.target.value })} />
            </div>
            <div className="form-group" style={{ display: "flex", alignItems: "center", gap: "8px" }}>
              <input type="checkbox" checked={form.firstOrderOnly} onChange={(e) => setForm({ ...form, firstOrderOnly: e.target.checked })} />
              <label style={{ margin: 0 }}>First order only</label>
            </div>
            {error && <p className="error-text">{error}</p>}
            <button type="submit" className="btn btn-primary btn-small">Create Coupon</button>
          </form>
        )}

        {loading && <p style={{ color: "var(--color-text-light)" }}>Loading coupons...</p>}
        {!loading && coupons.length === 0 && <p style={{ color: "var(--color-text-light)" }}>No coupons created yet.</p>}

        <div style={{ display: "flex", flexDirection: "column", gap: "10px" }}>
          {coupons.map((c) => (
            <div className="card" key={c.id} style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
              <div>
                <p style={{ fontWeight: 700, fontSize: "16px" }}>{c.code}</p>
                <p style={{ fontSize: "13px", color: "var(--color-text-light)" }}>{c.description}</p>
                <p style={{ fontSize: "13px", color: "var(--color-text-light)", marginTop: "4px" }}>
                  {c.discountType === "PERCENTAGE" ? `${c.discountValue}% off` : `₹${c.discountValue} off`}
                  {c.maxDiscountAmount && ` (max ₹${c.maxDiscountAmount})`}
                  {" · "}Min bill ₹{c.minBillAmount}
                  {c.firstOrderOnly && " · First order only"}
                </p>
                <p style={{ fontSize: "12px", color: "var(--color-text-light)" }}>
                  Used {c.usageCount}{c.usageLimit ? ` / ${c.usageLimit}` : ""} times · Expires {c.expiryDate}
                </p>
              </div>
              <span className={`status-badge ${c.active ? "status-badge-success" : "status-badge-error"}`}>
                {c.active ? "Active" : "Inactive"}
              </span>
            </div>
          ))}
        </div>
      </div>
    </>
  );
}

export default AdminCoupons;
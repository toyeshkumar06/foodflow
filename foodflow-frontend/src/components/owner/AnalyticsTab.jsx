import { useEffect, useState } from "react";
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, PieChart, Pie, Cell } from "recharts";
import axiosInstance from "../../api/axiosInstance";

const PIE_COLORS = ["#FF6B4A", "#4ADE9E", "#5B8DEF", "#F5A623", "#D65DB1", "#00C2A8"];

function AnalyticsTab({ restaurantId }) {
  const [data, setData] = useState(null);

  useEffect(() => {
    axiosInstance.get(`/restaurant-owner/restaurants/${restaurantId}/analytics`).then((res) => setData(res.data));
  }, [restaurantId]);

  if (!data) return <p style={{ color: "var(--color-text-light)" }}>Loading analytics...</p>;

  const chartData = data.last7Days.map((d) => ({
    date: d.date.slice(5), // MM-DD
    revenue: parseFloat(d.revenue),
    orders: d.orderCount,
  }));

  const pieData = data.topSellingItems.slice(0, 6).map((item) => ({
    name: item.name,
    value: item.quantitySold,
  }));

  return (
    <div>
      <div className="analytics-grid">
        <div className="card analytics-stat">
          <p className="analytics-stat-label">Total Revenue</p>
          <p className="analytics-stat-value">₹{data.totalRevenue}</p>
        </div>
        <div className="card analytics-stat">
          <p className="analytics-stat-label">Total Orders</p>
          <p className="analytics-stat-value">{data.totalOrders}</p>
        </div>
      </div>

      {chartData.length > 0 && (
        <div className="card" style={{ marginTop: "20px" }}>
          <h3 style={{ marginBottom: "16px" }}>Revenue — Last 7 Days</h3>
          <ResponsiveContainer width="100%" height={220}>
            <BarChart data={chartData}>
              <XAxis dataKey="date" stroke="var(--color-text-light)" fontSize={12} />
              <YAxis stroke="var(--color-text-light)" fontSize={12} />
              <Tooltip
                contentStyle={{ background: "var(--color-card)", border: "1px solid var(--color-border)", borderRadius: "8px" }}
                labelStyle={{ color: "var(--color-text)" }}
              />
              <Bar dataKey="revenue" fill="#FF6B4A" radius={[4, 4, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>
      )}

      {pieData.length > 0 && (
        <div className="card" style={{ marginTop: "20px" }}>
          <h3 style={{ marginBottom: "16px" }}>Top Selling Items</h3>
          <ResponsiveContainer width="100%" height={240}>
            <PieChart>
              <Pie data={pieData} dataKey="value" nameKey="name" cx="50%" cy="50%" outerRadius={80} label>
                {pieData.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={PIE_COLORS[index % PIE_COLORS.length]} />
                ))}
              </Pie>
              <Tooltip contentStyle={{ background: "var(--color-card)", border: "1px solid var(--color-border)", borderRadius: "8px" }} />
            </PieChart>
          </ResponsiveContainer>
        </div>
      )}

      <div className="card" style={{ marginTop: "20px" }}>
        <h3 style={{ marginBottom: "12px" }}>All Items</h3>
        {data.topSellingItems.length === 0 && <p style={{ color: "var(--color-text-light)" }}>No sales yet.</p>}
        {data.topSellingItems.map((item, i) => (
          <div key={i} style={{ display: "flex", justifyContent: "space-between", padding: "8px 0", borderBottom: "1px solid var(--color-border)" }}>
            <span>{item.name}</span>
            <span style={{ color: "var(--color-text-light)" }}>{item.quantitySold} sold</span>
          </div>
        ))}
      </div>
    </div>
  );
}

export default AnalyticsTab;
import { useAuth } from "../context/AuthContext";
import Navbar from "../components/Navbar";

function Home() {
  const { user } = useAuth();

  return (
    <>
      <Navbar />
      <div className="page-container" style={{ paddingTop: "60px" }}>
        <h1 style={{ fontSize: "32px", marginBottom: "8px" }}>
          Hey there 👋
        </h1>
        <p style={{ color: "var(--color-text-light)", fontSize: "16px" }}>
          You're logged in as <strong>{user?.email}</strong>. This is where your
          dashboard will come together as we build out the next pages.
        </p>
      </div>
    </>
  );
}

export default Home;
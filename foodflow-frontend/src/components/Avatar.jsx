const COLORS = [
  "#FF6B4A", "#4ADE9E", "#5B8DEF", "#F5A623", "#D65DB1", "#00C2A8", "#FF8A5B", "#7B61FF",
];

function getColorForName(name) {
  if (!name) return COLORS[0];
  let hash = 0;
  for (let i = 0; i < name.length; i++) {
    hash = name.charCodeAt(i) + ((hash << 5) - hash);
  }
  return COLORS[Math.abs(hash) % COLORS.length];
}

function getInitials(name) {
  if (!name) return "?";
  const parts = name.trim().split(" ");
  if (parts.length === 1) return parts[0].charAt(0).toUpperCase();
  return (parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
}

function Avatar({ name, size = 36 }) {
  const bg = getColorForName(name);
  return (
    <div
      className="avatar-circle"
      style={{ backgroundColor: bg, width: size, height: size, fontSize: size * 0.4 }}
      title={name}
    >
      {getInitials(name)}
    </div>
  );
}

export default Avatar;
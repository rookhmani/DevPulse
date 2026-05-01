export default function StatusBadge({ status }) {
  const value = status || 'PENDING';
  return <span className={`badge ${value.toLowerCase()}`}>{value}</span>;
}

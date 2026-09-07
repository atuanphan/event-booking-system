export default function LoadingSpinner({ size = 'md' }) {
  const sizeClass = { sm: 'w-5 h-5', md: 'w-8 h-8', lg: 'w-12 h-12' }[size];
  return (
    <div className="flex justify-center items-center py-12">
      <div className={`${sizeClass} border-4 border-indigo-200 border-t-indigo-600 rounded-full animate-spin`} />
    </div>
  );
}

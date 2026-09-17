export const useLoading = () => {
  const loading = ref(false);
  const withLoading = async (fn: () => Promise<any>) => {
    loading.value = true;
    try {
      return await fn();
    } finally {
      loading.value = false;
    }
  };
  return { loading, withLoading };
};
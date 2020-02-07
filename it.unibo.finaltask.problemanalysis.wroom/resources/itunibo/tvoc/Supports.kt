package itunibo.tvoc

interface ObservableSupport<T> {
	fun observe(handleData: (T) -> Unit)
}

interface TvocSupport : ObservableSupport<Pair<Int,Int>> { 
	
}
import { useState, useRef, useEffect } from "react";

const easeInOutQuad = (t: number) =>
	t < 0.5 ? 2 * t * t : 1 - (-2 * t + 2) * (t - 1);

interface AnimationState {
	value: number;
	targetValue: number;
	startTime: number | null;
}

type UseAnimationProps = number;

const useAnimation = (
	initialValue: UseAnimationProps
): {
	value: number;
	setValue: (targetValue: number, duration: number) => void;
	setValueNoAnimation: (targetValue: number) => void;
	cancelAnimation: () => void;
} => {
	const [state, setState] = useState<AnimationState>({
		value: initialValue,
		targetValue: initialValue,
		startTime: null,
	});
	const animationRef = useRef<number | null>(null);

	useEffect(() => {
		const tick = () => {
			const now = Date.now();
			if (state.startTime === null) return;
			const elapsed = now - state.startTime;
			const progress = Math.min(elapsed / animationRef.current!, 1);
			const newValue =
				state.value +
				(state.targetValue - state.value) * easeInOutQuad(progress);
			setState({ ...state, value: newValue });
			if (progress === 1) {
				setState({ ...state, startTime: null });
				animationRef.current = null;
			}
		};

		const id = window.requestAnimationFrame(tick);
		return () => {
			window.cancelAnimationFrame(id);
		};
	}, [state]);

	const setValue = (targetValue: number, duration: number) => {
		setState({ ...state, targetValue, startTime: Date.now() });
		animationRef.current = duration;
	};

	const setValueNoAnimation = (targetValue: number) => {
		setState({ ...state, value: targetValue });
	};

	const cancelAnimation = () => {
		setState({ ...state, startTime: null });
		animationRef.current = null;
	};

	return {
		value: state.value,
		setValue,
		setValueNoAnimation,
		cancelAnimation,
	};
};

export default useAnimation;

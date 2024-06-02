import { useState, useRef, useEffect, useCallback } from "react";

const easeInOutQuad = (t: number) => (t < 0.5 ? 2 * t * t : 1 - (-2 * t + 2) * (t - 1));

interface AnimationState {
	value: number;
	targetValue: number;
	startTime: number | null;
}

export type AnimationObject = {
	value: number;
	setValue: (targetValue: number, duration: number) => void;
	setValueNoAnimation: (targetValue: number) => void;
	cancelAnimation: () => void;
};

type UseAnimationProps = number;

const useAnimation = (initialValue: UseAnimationProps): AnimationObject => {
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
			let progress = elapsed / animationRef.current!;
			if (progress > 1) progress = 1; // Cap progress to 1
			let newValue = state.value + (state.targetValue - state.value) * easeInOutQuad(progress);
			newValue = progress === 1 ? state.targetValue : newValue;
			setState({ ...state, value: newValue });
			if (progress === 1) {
				setState({ ...state, startTime: null });
				animationRef.current = null;
				return;
			}
		};

		const id = window.requestAnimationFrame(tick);
		return () => {
			window.cancelAnimationFrame(id);
		};
	}, [state]);

	const setValue = useCallback(
		(targetValue: number, duration: number) => {
			setState({ ...state, targetValue, startTime: Date.now() });
			animationRef.current = duration;
		},
		[setState, state, animationRef]
	);

	const setValueNoAnimation = useCallback((targetValue: number) => {
		setState({ ...state, value: targetValue });
	},[setState, state]);

	const cancelAnimation = useCallback(() => {
		setState({ ...state, startTime: null });
		animationRef.current = null;
	}, [setState, state, animationRef]);

	return {
		value: state.value,
		setValue,
		setValueNoAnimation,
		cancelAnimation,
	};
};

export default useAnimation;

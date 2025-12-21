package com.trading.platform.trialing.strategies;

public class TrailingStrategyFactory {

	private TrailingStrategyFactory() {
		// Do Nothing
	}

	public static TrailingStrategy getTrialingStrategyByType(String trialingType) {
		TrailingType type = TrailingType.getType(trialingType);
		switch (type) {
		case DEFAULT:
			return new DefaultTrailingStrategy();
		case SIMPLE:
			return new SimpleTrailingStrategy();
		case PROTECTIVE:
			return new ProtectiveTrailingStrategy();
		case AGGRESSIVE:
			return new AggressiveTrailingStrategy();
		case CUSTOM_1:
			return new CustomMinimumProfitTrailingStrategy();
		default:
			return new DefaultTrailingStrategy();
		}
	}

	public static MultiTargetTrailingStrategy getMultiTargetTrialingStrategyByType(String trialingType) {
		MultiTargetTrailingType type = MultiTargetTrailingType.getType(trialingType);

		MultiTargetTrailingStrategy strategy = new DefaultMultiTargetTrailingStrategy();
		if (type.equals(MultiTargetTrailingType.MULTI_TARGET)) {
			strategy = new DefaultMultiTargetTrailingStrategy();
		}

		return strategy;
	}

}

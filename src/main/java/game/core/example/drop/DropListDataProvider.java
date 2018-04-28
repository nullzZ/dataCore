package game.core.example.drop;


import game.core.dataProvider.AbstractDataProvider;

/**
* DropListDataProvider 实体类
* create by GenerateDataProviderUtil 
*/ 
public class DropListDataProvider  extends AbstractDataProvider<DropListData> {
	private static final DropListDataProvider instance  = new DropListDataProvider();

	public static DropListDataProvider getInstance() {
		return instance;
	}

	private DropListDataProvider() {
	}

	@Override
	public String getFileName() {
		return "dropListData";
	}

}


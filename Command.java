package imetl;

public abstract class Command{
	private String name;
	
	protected Argparse parser;
	private BooleanFlag help;
	protected String helpManual;
	
	public Command(String name){
		this.name = name;
		this.parser = new Argparse();
	}
	
	public String getName(){return name;}
	
	
	
	public boolean execute(String[] args){
		help = new BooleanFlag("--help");
		parser.add(help);
		parser.parse(args);
		
		if(help.getBoolean()){
			System.out.println(helpManual);
			return true;
		}
		
		return execute();
	}
	
	protected abstract boolean execute();
}

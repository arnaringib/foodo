<?php
/**
 * @see PDO
 * @author siggijons
 *
 */
class FoodoPDO extends PDO {

	/**
	 * 
	 * @var FoodoPDO
	 */
	private static $instance;
	
	public static function getInstance() {
		if (self::$instance == null)
		{
			self::$instance = new FoodoPDO();
		}
		
		return self::$instance;
	}
	
    private function FoodoPDO($file = 'settings.ini')
    {
     	if (!$settings = parse_ini_file(APPLICATION_PATH . '/../conf/' . $file, TRUE)) throw new exception('Unable to open ' . $file . '.');
       
        $dns = $settings['database']['driver'] .
        		':host=' . $settings['database']['host'] .
				((!empty($settings['database']['port'])) ? (';port=' . $settings['database']['port']) : '') .
				';dbname=' . $settings['database']['schema'];
       
        parent::__construct($dns, $settings['database']['username'], $settings['database']['password']);
    }
    
    public function __clone()
    {
        trigger_error('Clone is not allowed.', E_USER_ERROR);
    }
}
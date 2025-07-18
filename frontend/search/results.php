<?php

if(!isset($_GET['s'])) {
	die('You must define a search term!');
}

$highlight = true;//highlight results or not
$search_in = array('html', 'htm');//allowable filetypes to search in
$search_dir = '..';//starting directory
$recursive = true;//should it search recursively or not
define('SIDE_CHARS', 80);
$file_count = 0;
$search_term = mb_strtolower($_GET['s'], 'UTF-8');
//$search_term = $_GET['s'];
$search_term_length = strlen($search_term);
$final_result = array();

$files = list_files($search_dir);

foreach($files as $file){
	$contents = file_get_contents($file);
	preg_match("/\<title\>(.*)\<\/title\>/", $contents, $page_title); //getting page title
	if (preg_match("#\<body.*\>(.*)\<\/body\>#si", $contents, $body_content)){ //getting content only between <body></body> tags
		$clean_content = strip_tags($body_content[0]); //remove html tags
		$clean_content = preg_replace( '/\s+/', ' ', $clean_content ); //remove duplicate whitespaces, carriage returns, tabs, etc
	
	//$found = strpos_recursive($clean_content, $search_term);
	$found = strpos_recursive(mb_strtolower($clean_content, 'UTF-8'), $search_term);
	$final_result[$file_count]['page_title'][] = $page_title[1];
	$final_result[$file_count]['file_name'][] = $file;
}
	if($found && !empty($found)) {
		for ($z = 0; $z < count($found[0]); $z++){
			$pos = $found[0][$z][1];
			$side_chars = SIDE_CHARS;
			if ($pos < SIDE_CHARS){
				$side_chars = $pos;
				$pos_end = SIDE_CHARS + $search_term_length;
			}else{
				$pos_end = SIDE_CHARS*2 + $search_term_length;
			}

			$pos_start = $pos - $side_chars;
			$str = substr($clean_content, $pos_start, $pos_end);
			$result = preg_replace('#'.$search_term.'#ui', '<span class="search">\0</span>', $str);
			//$result = preg_replace('#'.$search_term.'#ui', '<span class="search">'.$search_term.'</span>', $str);
			$final_result[$file_count]['search_result'][] = $result;
		}
	} else {
		$final_result[$file_count]['search_result'][] = '';
	}
	$file_count++;
}
?>
<!DOCTYPE html>
<html lang="en">

<head>

    <!-- metas -->
    <meta charset="utf-8">
    <meta name="author" content="Chitrakoot Web" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <meta name="keywords" content="Online Education Learning Template" />
    <meta name="description" content="eLearn - Online Education Learning Template" />

    <!-- title  -->
    <title>eLearn - Multipurpose Business and Admin Template</title>

    <!-- favicon -->
    <link rel="shortcut icon" href="../img/logos/favicon.png">
    <link rel="apple-touch-icon" href="../img/logos/apple-touch-icon-57x57.png">
    <link rel="apple-touch-icon" sizes="72x72" href="../img/logos/apple-touch-icon-72x72.png">
    <link rel="apple-touch-icon" sizes="114x114" href="../img/logos/apple-touch-icon-114x114.png">

    <!-- plugins -->
    <link rel="stylesheet" href="../css/plugins.css" />

    <!-- search css -->
    <link rel="stylesheet" href="search.css" />

    <!-- core style css -->
    <link href="../css/styles.css" rel="stylesheet" />

    <!-- custom style css -->
    <link href="../css/custom.css" rel="stylesheet" />

</head>

<body>
 
<script type="text/javascript">
;(function(){	
	document.body.onload=resize
	window.onresize=resize
	
	function resize(){
		parent._resize(document.getElementById('search-results').offsetHeight)
	}
})()
</script>

<div class="search-frame">
	<div id="search-results">
		<ol class="search_list">
	<?php
		$match_count = 0;
		for ($i=0; $i < count($final_result); $i++){
			if (!empty($final_result[$i]['search_result'][0]) || $final_result[$i]['search_result'][0] !== ''){
				$match_count++;
	?>
			<li class="result-item">
				<h4 class="search_title"><a target="_top" href="<?php echo $final_result[$i]['file_name'][0]; ?>" class="search_link"> <?php echo $final_result[$i]['page_title'][0]; ?> </a></h4>
				...<?php echo $final_result[$i]['search_result'][0]; ?>...
				<span class="match">Terms matched: <?php echo count($final_result[$i]['search_result']); ?> - URL: <?php echo $final_result[$i]['file_name'][0]; ?></span>
			</li>
	<?php
			}
		}
		if ($match_count == 0) {
			echo '<h4>No results found for <span class="search">'.$search_term.'</span></h4>';
		}
	?>
		</ol>
	</div>
</div>

</body>
</html>


<?php
//lists all the files in the directory given (and sub-directories if it is enabled)
function list_files($dir){
	global $recursive, $search_in;

	$result = array();
	if(is_dir($dir)){
		if($dh = opendir($dir)){
			while (($file = readdir($dh)) !== false) {
				if(!($file == '.' || $file == '..')){
					$file = $dir.'/'.$file;
					if(is_dir($file) && $recursive == true && $file != './.' && $file != './..'){
						$result = array_merge($result, list_files($file));
					}
					else if(!is_dir($file)){
						if(in_array(get_file_extension($file), $search_in)){
							$result[] = $file;
						}
					}
				}
			}
		}
	}
	return $result;
}

//returns the extention of a file
function get_file_extension($filename){
	$result = '';
	$parts = explode('.', $filename);
	if(is_array($parts) && count($parts) > 1){
		$result = end($parts);
	}
	return $result;
}

function strpos_recursive($haystack, $needle, $offset = 0, &$results = array()) {               
    $offset = stripos($haystack, $needle, $offset);
    if($offset === false) {
        return $results;           
    } else {
        $pattern = '/'.$needle.'/ui';
	preg_match_all($pattern, $haystack, $results, PREG_OFFSET_CAPTURE);
		return $results;
    }
}
?>
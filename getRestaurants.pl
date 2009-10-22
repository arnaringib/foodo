#!/usr/bin/env perl

use warnings;
use HTML::Tree;
use LWP::Simple;
use utf8;

my $url = "http://veitingastadir.is/matsolustadir/";
my $content = get($url);

my $tree = HTML::Tree->new();

$tree->parse($content);

my @divs = $tree->look_down( _tag => q{div});
my @imgs = $tree->look_down( _tag => img);
my ($title);
my ($address);
my ($email);
print "<?xml version='1.0' encoding='utf-8'?> \n";
print "<info> \n";
for my $div (@divs){

	if($div->attr(q{class}) eq q{item-content-title}) {
		print " <restaurant> \n"; 
		$title = $div->as_text;
			utf8::encode($title);
			print "  <title>";
			print $title;
			print "</title> \n";
			next;
	}
	if($div->attr(q{class}) eq q{item-content-left}) {
		$address = $div->as_text;
		utf8::encode($address);
		if ($address =~ m/(.*?)(\d\d\d)(.\D*)(\d{3}.\d*)/){
			print "   <address>$1</address> \n";
			print "   <zip>$2</zip> \n";
			print "   <city>$3</city> \n";
			print "   <phone>$4</phone> \n";
			next;
		}
		
	}
		if($div->attr(q{class}) eq q{item-content-right}) {
			$email = $div->as_text;
			utf8::encode($email);
			if($email =~ m/(.*?\.is)(.*)/){
				print "   <website>$1</website> \n";
				print "   <email>$2</email> \n";
				print " </restaurant> \n";
				next;
			}
			
		}
		
}
print "</info> \n";

readline;

$tree->delete;
 